package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.PartnerNetworkService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("partnerNetworkSecurityService")
@Transactional(readOnly = true)
public class PartnerNetworkSecurityServiceImpl extends BaseSecurityService implements PartnerNetworkSecurityService {

    @Autowired
    private PartnerNetworkService partnerNetworkService;

    @Autowired
    private CommunityService communityService;

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            ROLE_SUPER_ADMINISTRATOR,
            PARTNER_NETWORK_VIEW_IF_ASSOCIATED_ORGANIZATION,
            PARTNER_NETWORK_VIEW_IF_ASSOCIATED_COMMUNITY,
            PARTNER_NETWORK_VIEW_IF_CO_REGULAR_COMMUNITY_CTM,
            PARTNER_NETWORK_VIEW_IF_CO_REGULAR_CLIENT_CTM,
            PARTNER_NETWORK_VIEW_IF_CLIENT_ADDED_BY_SELF);

    @Override
    public boolean canView(Long id) {
        var filter = currentUserFilter();

        //also check eligible?

        if (filter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        var network = partnerNetworkService.findSecurityByPartnerNetworkId(id);
        var networkCommunities = Lazy.of(() -> CareCoordinationUtils.getCommunityIdsSet(network));
        var networkOrganizations = Lazy.of(() ->
                CareCoordinationUtils.getOrganizationIdsSet(
                        communityService.findSecurityAwareEntities(networkCommunities.get())
                ));

        if (filter.hasPermission(PARTNER_NETWORK_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(PARTNER_NETWORK_VIEW_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderAnyOrganization(employees, networkOrganizations.get())) {
                return true;
            }
        }

        if (filter.hasPermission(PARTNER_NETWORK_VIEW_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(PARTNER_NETWORK_VIEW_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderAnyCommunity(employees, networkCommunities.get())) {
                return true;
            }
        }

        if (filter.hasPermission(PARTNER_NETWORK_VIEW_IF_CO_REGULAR_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(PARTNER_NETWORK_VIEW_IF_CO_REGULAR_COMMUNITY_CTM);

            if (isAnyInAnyCommunityCareTeam(
                    employees,
                    networkCommunities.get(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(PARTNER_NETWORK_VIEW_IF_CO_REGULAR_CLIENT_CTM)) {
            var employees = filter.getEmployees(PARTNER_NETWORK_VIEW_IF_CO_REGULAR_CLIENT_CTM);

            if (networkCommunities.get().stream()
                    .anyMatch(communityId -> isAnyInAnyClientCareTeamOfCommunity(
                            employees,
                            communityId,
                            AffiliatedCareTeamType.REGULAR,
                            HieConsentCareTeamType.currentAndOnHold()))) {
                return true;
            }
        }

        if (filter.hasPermission(PARTNER_NETWORK_VIEW_IF_CLIENT_ADDED_BY_SELF)) {
            var employees = filter.getEmployees(PARTNER_NETWORK_VIEW_IF_CLIENT_ADDED_BY_SELF);

            if (clientService.existsCreatedByAnyInCommunityIds(employees, networkCommunities.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }
}
