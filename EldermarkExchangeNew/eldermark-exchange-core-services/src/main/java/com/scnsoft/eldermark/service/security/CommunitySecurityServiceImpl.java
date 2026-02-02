package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.EligibleForDiscoveryAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIsSignatureEnabledAware;
import com.scnsoft.eldermark.beans.security.projection.dto.CommunitySecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationSecurityAwareEntity;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.DeviceTypeDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.CommunityPictureService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("communitySecurityService")
@Transactional(readOnly = true)
public class CommunitySecurityServiceImpl extends BaseSecurityService implements CommunitySecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            COMMUNITY_VIEW_IN_LIST_ALL,
            COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_ORGANIZATION,
            COMMUNITY_VIEW_IN_LIST_IF_ASSOCIATED_COMMUNITY,
            COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_ORGANIZATION,
            COMMUNITY_VIEW_IN_LIST_IF_FROM_AFFILIATED_COMMUNITY,
            COMMUNITY_VIEW_IN_LIST_IF_CO_RP_COMMUNITY_CTM,
            COMMUNITY_VIEW_IN_LIST_IF_CO_RP_CLIENT_CTM,
            COMMUNITY_VIEW_IN_LIST_IF_EXTERNAL_REFERRAL_REQUEST);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DeviceTypeDao deviceTypeDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private CommunityPictureService communityPictureService;

    @Autowired
    private MarketplaceCommunitySecurityService marketplaceCommunitySecurityService;

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Override
    public boolean canAdd(CommunitySecurityFieldsAware dto) {
        var filter = currentUserFilter();

        if (filter.hasPermission(COMMUNITY_ADD_ALL)) {
            return true;
        }

        if (filter.hasPermission(COMMUNITY_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, dto.getOrganizationId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEdit(Long communityId) {
        var community = communityService.findSecurityAwareEntity(communityId);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(COMMUNITY_EDIT_ALL)) {
            return true;
        }

        if (filter.hasPermission(COMMUNITY_EDIT_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_EDIT_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_EDIT_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_EDIT_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEditSignatureConfig(Long communityId) {

        var community = communityService.findById(communityId, CommunitySignatureSecurityConfigAware.class);

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        return canEditSignatureConfig(
                community.getOrganizationId(),
                community.getOrganizationIsSignatureEnabled()
        );
    }

    @Override
    public boolean canEditSignatureConfigInOrganization(Long organizationId) {

        var organization = organizationService.findById(organizationId, OrganizationSignatureSecurityConfigAware.class);

        return canEditSignatureConfig(
                organization.getId(),
                organization.getIsSignatureEnabled()
        );
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long communityId) {
        var community = communityService.findSecurityAwareEntity(communityId);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_ALL)) {
            return true;
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_DETAILS_IF_CO_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_DETAILS_IF_CO_RP_CLIENT_CTM);
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
    public boolean canViewAll(Collection<Long> communityIds) {
        return communityIds.stream().allMatch(this::canView);
    }

    @Override
    public boolean canViewByDeviceTypeId(Long deviceId) {
        return canView(deviceTypeDao.getOne(deviceId).getCommunity().getId());
    }

    @Override
    public boolean hasAccessibleClient(Long communityId) {
        var permissionFilter = currentUserFilter();

        var hasClientAccess = clientSpecificationGenerator.hasDetailsAccess(permissionFilter);
        var byCommunityId = clientSpecificationGenerator.byCommunityId(communityId);

        return clientDao.count(byCommunityId.and(hasClientAccess)) > 0;
    }

    @Override
    public boolean hasAccessibleClient(Collection<Long> communityIds) {
        return communityIds.stream().allMatch(this::hasAccessibleClient);
    }

    @Override
    public boolean canDownloadPicture(Long pictureId) {
        var communityId = communityPictureService.findCommunityIdAwareById(pictureId).getCommunityId();
        return canView(communityId) || marketplaceCommunitySecurityService.canViewByCommunityId(communityId);
    }

    @Override
    public boolean canViewLogo(Long communityId) {
        var community = communityService.findSecurityAwareEntity(communityId);
        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var filter = currentUserFilter();

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_ALL)) {
            return true;
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_CO_RP_COMMUNITY_CTM)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_CO_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_CO_RP_CLIENT_CTM)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_CO_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_CLIENT_CTM_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_CLIENT_CTM_ASSOCIATED_COMMUNITY);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(filter, employees);
            if (clientCareTeamMemberService.existAccessibleCareTeamMemberInCommunity(adjustedFilter, communityId, HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_COMMUNITY_CTM_ASSOCIATED_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_COMMUNITY_CTM_ASSOCIATED_COMMUNITY);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(filter, employees);
            if (communityCareTeamMemberService.existAccessibleCareTeamMemberInCommunity(adjustedFilter, communityId, HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_MEDICATION_IN_COMMUNITY)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_ACCESSIBLE_MEDICATION_IN_COMMUNITY);
            var adjustedFilter = PermissionFilterUtils.filterWithEmployeesOnly(filter, employees);
            if (clientMedicationService.existsInCommunity(adjustedFilter, communityId)) {
                return true;
            }
        }

        if (filter.hasPermission(COMMUNITY_VIEW_LOGO_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION)) {
            var employees = filter.getEmployees(COMMUNITY_VIEW_LOGO_IF_FROM_PARTNER_COMMUNITY_ORGANIZATION);
            return isAnyCreatedInPartnerCommunityOrganization(employees, communityId);
        }

        return false;
    }

    private boolean canEditSignatureConfig(Long organizationId, Boolean organizationIsSignatureEnabled) {

        if (!organizationIsSignatureEnabled) {
            return false;
        }

        var permissionFilter = currentUserFilter();
        if (permissionFilter.hasPermission(COMMUNITY_EDIT_SIGNATURE_CONFIG_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(COMMUNITY_EDIT_SIGNATURE_CONFIG_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(COMMUNITY_EDIT_SIGNATURE_CONFIG_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, organizationId)) {
                return true;
            }
        }

        return false;
    }

    private interface CommunitySignatureSecurityConfigAware extends OrganizationIdAware, EligibleForDiscoveryAware {

        Boolean getOrganizationIsSignatureEnabled();
    }

    private interface OrganizationSignatureSecurityConfigAware extends IdAware, OrganizationIsSignatureEnabledAware {
    }
}
