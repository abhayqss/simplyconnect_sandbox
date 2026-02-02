package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.security.projection.dto.LabSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.service.OrganizationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("labSecurityService")
@Transactional(readOnly = true)
public class LabSecurityServiceImpl extends BaseSecurityService implements LabSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            LAB_VIEW_SENT_ALL_EXCEPT_OPTED_OUT,
            LAB_VIEW_SENT_IF_ASSOCIATED_ORGANIZATION,
            LAB_VIEW_SENT_IF_ASSOCIATED_COMMUNITY,
            LAB_VIEW_SENT_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            LAB_VIEW_SENT_IF_CURRENT_REGULAR_CLIENT_CTM,
            LAB_VIEW_SENT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,

            LAB_PARTLY_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT,
            LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION,
            LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY,
            LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM,
            LAB_PARTLY_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,

            LAB_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
            LAB_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
            LAB_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
            LAB_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
            LAB_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
            LAB_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR,

            LAB_VIEW_REVIEWED_ALL_EXCEPT_OPTED_OUT,
            LAB_VIEW_REVIEWED_IF_ASSOCIATED_ORGANIZATION,
            LAB_VIEW_REVIEWED_IF_ASSOCIATED_COMMUNITY,
            LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_COMMUNITY_CTM,
            LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_CLIENT_CTM,
            LAB_VIEW_REVIEWED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            LAB_VIEW_REVIEWED_IF_SELF_RECORD
    );

    @Autowired
    private CommunityService communityService;

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public boolean canAdd(LabSecurityFieldsAware dto) {
        var client = clientService.findSecurityAwareEntity(dto.getClientId());

        return hasAccessToLabs(client,
                LAB_ADD_ALL_EXCEPT_OPTED_OUT,
                LAB_ADD_IF_ASSOCIATED_ORGANIZATION,
                LAB_ADD_IF_ASSOCIATED_COMMUNITY,
                LAB_ADD_IF_CO_REGULAR_COMMUNITY_CTM,
                LAB_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
    }

    @Override
    public boolean canAddToCommunity(Long communityId) {
        var community = communityService.findSecurityAwareEntity(communityId);
        var organization = organizationService.findSecurityAware(community.getOrganizationId());
        if (!organization.getLabsEnabled()) {
            return false;
        }

        if (!isEligibleForDiscoveryCommunity(community)) {
            return false;
        }

        var anyClientExistsInCommunity = Lazy.of(() -> clientService.existInCommunity(communityId));
        var anyOptedInClientExistsInCommunity = Lazy.of(() -> clientService.existOptedInInCommunity(communityId));

        if (!clientService.existInCommunity(communityId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(LAB_ADD_ALL_EXCEPT_OPTED_OUT) && anyOptedInClientExistsInCommunity.get()) {
            return true;
        }

        if (permissionFilter.hasPermission(LAB_ADD_IF_ASSOCIATED_ORGANIZATION) && anyClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(LAB_ADD_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_ADD_IF_ASSOCIATED_COMMUNITY) && anyClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(LAB_ADD_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_ADD_IF_CO_REGULAR_COMMUNITY_CTM) && anyClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(LAB_ADD_IF_CO_REGULAR_COMMUNITY_CTM);

            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentAndOnHold())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_ADD_IF_CURRENT_REGULAR_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(LAB_ADD_IF_CURRENT_REGULAR_CLIENT_CTM);

            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId, 
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(LAB_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);

            if (isAnyClientOptedInAndAddedBySelfInCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList(Long organizationId) {
        var organization = organizationService.findSecurityAware(organizationId);
        if (!organization.getLabsEnabled()) {
            return false;
        }

        if (!organizationService.hasEligibleForDiscoveryOrNoVisibleCommunities(organizationId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();
        return permissionFilter.hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long labOrderId) {
        var labOrder = labResearchOrderService.findSecurityAware(labOrderId);
        var client = clientService.findSecurityAwareEntity(labOrder.getClientId());

        switch (labOrder.getStatus()) {
            case REVIEWED:
                return hasAccessToReviewedLabs(client);

            case SENT_TO_LAB:
                return hasAccessToSentLabs(client);

            case PENDING_REVIEW:
                return hasCoordinatorAccessToPendingLabs(client) ||
                        hasPartialAccessToPendingLabs(client);
        }

        return false;
    }

    @Override
    public boolean canReview(Long labOrderId) {
        var labOrder = labResearchOrderService.findSecurityAware(labOrderId);
        var client = clientService.findSecurityAwareEntity(labOrder.getClientId());
        if (labOrder.getStatus() == LabResearchOrderStatus.PENDING_REVIEW) {
            return hasAccessToLabs(client,
                    LAB_REVIEW_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
                    LAB_REVIEW_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
                    LAB_REVIEW_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
                    LAB_REVIEW_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
                    LAB_REVIEW_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
                    LAB_REVIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR);
        }
        return false;
    }

    @Override
    public boolean canViewLabs() {
        var permissionFilter = currentUserFilter();
        return organizationService.existAccessibleOrganizationsWithLabsEnabled(permissionFilter);
    }

    @Override
    public boolean canReviewInOrganization(Long organizationId) {
        var organization = organizationService.findSecurityAware(organizationId);
        if (!organization.getLabsEnabled()) {
            return false;
        }

        if (!organizationService.hasEligibleForDiscoveryOrNoVisibleCommunities(organizationId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(LAB_REVIEW_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR)
                && clientService.existOptedInInOrganization(organizationId)) {
            return true;
        }

        //todo discuss why associated community has the same check as organization permission
        if (permissionFilter.hasAnyPermission(Set.of(LAB_REVIEW_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR, LAB_REVIEW_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR))) {
            var employees = permissionFilter.getEmployeesWithAny(Set.of(LAB_REVIEW_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR, LAB_REVIEW_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR));
            if (isAnyCreatedUnderOrganization(employees, organization.getId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_REVIEW_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR)) {
            var employees = permissionFilter.getEmployees(LAB_REVIEW_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR);
            if (isAnyInAnyCommunityCareTeamOfOrganization(
                    employees,
                    organization.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)))
                return true;
        }

        if (permissionFilter.hasPermission(LAB_REVIEW_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR)) {
            var employees = permissionFilter.getEmployees(LAB_REVIEW_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR);

            if (isAnyInAnyClientCareTeamOfOrganization(
                    employees,
                    organization.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(LAB_REVIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR)) {
            var employees = permissionFilter.getEmployees(LAB_REVIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR);

            if (isAnyClientOptedInAndAddedBySelfInOrganization(employees, organization.getId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canReview(Collection<Long> labOrderIds) {
        if (CollectionUtils.isEmpty(labOrderIds)) {
            return false;
        }
        for (Long labOrderId : labOrderIds) {
            if (!canReview(labOrderId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canViewResults(Long labOrderId) {
        var labOrder = labResearchOrderService.findSecurityAware(labOrderId);
        var client = clientService.findSecurityAwareEntity(labOrder.getClientId());

        switch (labOrder.getStatus()) {
            case REVIEWED:
                return hasAccessToReviewedLabs(client);

            case PENDING_REVIEW:
                return hasCoordinatorAccessToPendingLabs(client);
        }

        return false;
    }

    private boolean hasAccessToLabs(ClientSecurityAwareEntity client,
                                    Permission allExceptOptedOutAccess,
                                    Permission associatedOrganization,
                                    Permission associatedCommunity,
                                    Permission currentRegularCommunityCtm,
                                    Permission currentRegularClientCtm,
                                    Permission optedInAddedBySelf) {
        return hasAccessToLabs(
                client,
                allExceptOptedOutAccess,
                associatedOrganization,
                associatedCommunity,
                currentRegularCommunityCtm,
                currentRegularClientCtm,
                optedInAddedBySelf,
                null
        );
    }

    private boolean hasAccessToReviewedLabs(ClientSecurityAwareEntity client) {
        return hasAccessToLabs(client,
                LAB_VIEW_REVIEWED_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_REVIEWED_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_REVIEWED_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_REVIEWED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                LAB_VIEW_REVIEWED_IF_SELF_RECORD
        );
    }

    private boolean hasAccessToSentLabs(ClientSecurityAwareEntity client) {
        return hasAccessToLabs(client,
                LAB_VIEW_SENT_ALL_EXCEPT_OPTED_OUT,
                LAB_VIEW_SENT_IF_ASSOCIATED_ORGANIZATION,
                LAB_VIEW_SENT_IF_ASSOCIATED_COMMUNITY,
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_VIEW_SENT_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_VIEW_SENT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF
        );
    }

    private boolean hasCoordinatorAccessToPendingLabs(ClientSecurityAwareEntity client) {
        return hasAccessToLabs(client,
                LAB_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT_IF_COORDINATOR,
                LAB_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM_AND_COORDINATOR,
                LAB_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF_AND_COORDINATOR
        );
    }

    private boolean hasPartialAccessToPendingLabs(ClientSecurityAwareEntity client) {
        return hasAccessToLabs(client,
                LAB_PARTLY_VIEW_PENDING_ALL_EXCEPT_OPTED_OUT,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_ORGANIZATION,
                LAB_PARTLY_VIEW_PENDING_IF_ASSOCIATED_COMMUNITY,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                LAB_PARTLY_VIEW_PENDING_IF_CURRENT_REGULAR_CLIENT_CTM,
                LAB_PARTLY_VIEW_PENDING_IF_OPTED_IN_CLIENT_ADDED_BY_SELF
        );
    }

    private boolean hasAccessToLabs(ClientSecurityAwareEntity client,
                                    Permission allExceptOptedOut,
                                    Permission associatedOrganization,
                                    Permission associatedCommunity,
                                    Permission currentRegularCommunityCtm,
                                    Permission currentRegularClientCtm,
                                    Permission optedInAddedBySelf,
                                    Permission selfRecord) {
        var organization = organizationService.findSecurityAware(client.getOrganizationId());
        if (!organization.getLabsEnabled()) {
            return false;
        }

        if (!isInEligibleForDiscoveryCommunity(client)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(allExceptOptedOut) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(associatedOrganization)) {
            var employees = permissionFilter.getEmployees(associatedOrganization);

            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(associatedCommunity)) {
            var employees = permissionFilter.getEmployees(associatedCommunity);

            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(currentRegularCommunityCtm)) {
            var employees = permissionFilter.getEmployees(currentRegularCommunityCtm);

            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(currentRegularClientCtm)) {
            var employees = permissionFilter.getEmployees(currentRegularClientCtm);

            if (isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(optedInAddedBySelf)) {
            var employees = permissionFilter.getEmployees(optedInAddedBySelf);

            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (selfRecord != null && permissionFilter.hasPermission(selfRecord)) {
            var employees = permissionFilter.getEmployees(selfRecord);

            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        return false;
    }
}
