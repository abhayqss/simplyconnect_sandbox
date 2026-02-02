package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.EligibleForDiscoveryAware;
import com.scnsoft.eldermark.beans.projection.EligibleForDiscoveryMarketplaceEnabledAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("marketplaceCommunitySecurityService")
@Transactional(readOnly = true)
public class MarketplaceCommunitySecurityServiceImpl extends BaseSecurityService implements MarketplaceCommunitySecurityService {

    private static List<Permission> VIEW_LIST_PERMISSION = List.of(
            MARKETPLACE_VIEW_ALL,
            MARKETPLACE_VIEW_IF_ASSOCIATED_ORGANIZATION,
            MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION
    );

    @Autowired
    private CommunityService communityService;

    @Override
    public boolean canConfigure(Long communityId) {
        var organizationId = Optional.ofNullable(communityId)
                .map(id -> communityService.findById(id, OrganizationIdAware.class))
                .map(OrganizationIdAware::getOrganizationId)
                .orElse(null);

        return hasPermissions(
                communityId,
                organizationId,
                COMMUNITY_CONFIGURE_MARKETPLACE_ALL,
                COMMUNITY_CONFIGURE_MARKETPLACE_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_CONFIGURE_MARKETPLACE_IF_ASSOCIATED_COMMUNITY
        );
    }

    @Override
    public boolean canConfigureInOrganization(Long organizationId) {
        return hasPermissions(
                null,
                organizationId,
                COMMUNITY_CONFIGURE_MARKETPLACE_ALL,
                COMMUNITY_CONFIGURE_MARKETPLACE_IF_ASSOCIATED_ORGANIZATION,
                COMMUNITY_CONFIGURE_MARKETPLACE_IF_ASSOCIATED_COMMUNITY
        );
    }

    @Override
    public boolean canViewList() {
        var filter = currentUserFilter();
        return filter.hasAnyPermission(VIEW_LIST_PERMISSION);
    }

    @Override
    public boolean canViewByCommunityId(Long communityId) {
        var community = communityService.findById(communityId, EligibleForDiscoveryMarketplaceEnabledAware.class);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (!filter.hasPermission(MARKETPLACE_VIEW_NOT_DISCOVERABLE_ALLOWED)
                && !Objects.equals(Boolean.TRUE, community.getMarketplaceDiscoverable())) {
            return false;
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_ALL)) {
            return true;
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION);
            return isAnyCreatedInPartnerCommunityOrganization(employees, communityId);
        }

        return isFeaturedServiceProviderOfAccessibleCommunity(communityId, filter);
    }

    @Override
    public boolean canViewPartnerProviders(Long communityId) {

        var community = communityService.findById(communityId, EligibleForDiscoveryMarketplaceEnabledAware.class);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (!filter.hasPermission(MARKETPLACE_VIEW_NOT_DISCOVERABLE_ALLOWED)
                && !Objects.equals(Boolean.TRUE, community.getMarketplaceDiscoverable())) {
            return false;
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_ALL)) {
            return true;
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION);
            return isAnyCreatedInPartnerCommunityOrganization(employees, communityId);
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_CO_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_PARTNER_PROVIDER_IF_CO_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewFeaturedPartnerProviders(Long communityId) {
        var community = communityService.findById(communityId, EligibleForDiscoveryAware.class);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_ALL)) {
            return true;
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_CO_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(MARKETPLACE_VIEW_FEATURED_PARTNER_PROVIDER_IF_CO_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEditFeaturedPartnerProviders(Long communityId, Long organizationId) {
        return hasPermissions(
                communityId,
                organizationId,
                ROLE_SUPER_ADMINISTRATOR,
                MARKETPLACE_EDIT_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_ORGANIZATION,
                MARKETPLACE_EDIT_FEATURED_PARTNER_PROVIDER_IF_ASSOCIATED_COMMUNITY
        );
    }

    private boolean hasPermissions(
            Long communityId,
            Long organizationId,
            Permission all,
            Permission ifAssociatedOrganization,
            Permission ifAssociatedCommunity
    ) {
        var filter = currentUserFilter();

        if (filter.hasPermission(all)) {
            return true;
        }

        if (organizationId != null && ifAssociatedOrganization != null && filter.hasPermission(ifAssociatedOrganization)) {
            var employees = filter.getEmployees(ifAssociatedOrganization);

            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        if (communityId != null && ifAssociatedCommunity != null && filter.hasPermission(ifAssociatedCommunity)) {
            var employees = filter.getEmployees(ifAssociatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }
}
