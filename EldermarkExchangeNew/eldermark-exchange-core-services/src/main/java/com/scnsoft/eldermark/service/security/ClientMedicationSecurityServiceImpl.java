package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;
import com.scnsoft.eldermark.entity.AccessRight;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientMedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("medicationSecurityService")
@Transactional(readOnly = true)
public class ClientMedicationSecurityServiceImpl extends AccessFlagsCheckingSecurityService implements ClientMedicationSecurityService {

    private static final PermissionScopeProvider permissionsScope = AccessFlagsCheckingSecurityService.buildScopeProvider(
            MEDICATION_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
            MEDICATION_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            MEDICATION_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            MEDICATION_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            MEDICATION_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            MEDICATION_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            MEDICATION_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            MEDICATION_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            MEDICATION_VIEW_MERGED_IF_SELF_RECORD,
            null,
            MEDICATION_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = permissionsScope.getAllPermissions();

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Override
    public boolean canView(Long id) {
        var medication = clientMedicationService.findSecurityAwareEntity(id);
        return canViewByClientOrMerged(medication.getClientId(),
                permissionsScope,
                AccessRight.Code.MY_PHR, AccessRight.Code.MEDICATIONS_LIST);
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    /* (non-Javadoc)
     *
     * @see CcdEntitySecurityService#canViewOfClientIfPresent(ClientIdAware)
     */
    @Override
    public boolean canViewOfClientIfPresent(ClientIdAware filter) {
        return filter.getClientId() == null || canViewOfClient(filter.getClientId());
    }

    @Override
    public boolean canViewOfClient(Long clientId) {
        return canViewByClientOrMerged(clientId,
                permissionsScope,
                AccessRight.Code.MY_PHR, AccessRight.Code.MEDICATIONS_LIST);
    }

    @Override
    public boolean canAdd(ClientMedicationSecurityAwareEntity medication) {
        return hasPermissions(
                medication,
                MEDICATION_ADD_ALL_EXCEPT_OPTED_OUT,
                MEDICATION_ADD_IF_ASSOCIATED_ORGANIZATION,
                MEDICATION_ADD_IF_ASSOCIATED_COMMUNITY,
                MEDICATION_ADD_IF_OPTED_IN_FROM_AFFILIATED_ORGANIZATION,
                MEDICATION_ADD_IF_OPTED_IN_FROM_AFFILIATED_COMMUNITY,
                MEDICATION_ADD_IF_CURRENT_RP_COMMUNITY_CTM,
                MEDICATION_ADD_IF_CURRENT_RP_CLIENT_CTM,
                MEDICATION_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                MEDICATION_ADD_IF_CLIENT_FOUND_IN_RECORD_SEARCH
        );
    }

    @Override
    public boolean canEdit(Long id) {
        var medication = clientMedicationService.findSecurityAwareEntity(id);
        return canEdit(medication);
    }

    @Override
    public boolean canEdit(ClientMedicationSecurityAwareEntity medication) {
        if (Boolean.TRUE.equals(medication.getIsManuallyCreated())) {
            return hasPermissions(
                    medication,
                    MEDICATION_EDIT_ALL_EXCEPT_OPTED_OUT,
                    MEDICATION_EDIT_IF_ASSOCIATED_ORGANIZATION,
                    MEDICATION_EDIT_IF_ASSOCIATED_COMMUNITY,
                    MEDICATION_EDIT_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                    MEDICATION_EDIT_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                    MEDICATION_EDIT_IF_CURRENT_RP_COMMUNITY_CTM,
                    MEDICATION_EDIT_IF_CURRENT_RP_CLIENT_CTM,
                    MEDICATION_EDIT_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                    MEDICATION_EDIT_IF_CLIENT_FOUND_IN_RECORD_SEARCH
            );
        } else {
            return false;
        }
    }

    private boolean hasPermissions(
            ClientMedicationSecurityAwareEntity medication,
            Permission allExceptOptedOut,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission optedInIfFromAffiliatedOrganizationPermission,
            Permission optedInIfFromAffiliatedCommunityPermission,
            Permission ifCurrentRpCommunityCtmPermission,
            Permission ifCurrentRpClientCtmPermission,
            Permission ifClientAddedBySelfPermission,
            Permission ifClientFoundInRecordSearchPermission
    ) {

        var permissionFilter = currentUserFilter();

        var client = clientService.findSecurityAwareEntity(medication.getClientId());

        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(allExceptOptedOut) && isClientOptedIn(client)) {
            return true;
        }

        if (permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);

            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);

            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(optedInIfFromAffiliatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedOrganizationPermission);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId()) && isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(optedInIfFromAffiliatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedCommunityPermission);
            if (isAnyInAffiliatedCommunity(employees, client.getCommunityId()) && isClientOptedIn(client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifCurrentRpCommunityCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpCommunityCtmPermission);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifCurrentRpClientCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpClientCtmPermission);
            if (isAnyInClientCareTeam(
                    employees,
                    client,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifClientAddedBySelfPermission)) {
            var employees = permissionFilter.getEmployees(ifClientAddedBySelfPermission);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(ifClientFoundInRecordSearchPermission)
                && permissionFilter.containsClientRecordSearchFoundId(medication.getClientId())
        ) {
            return true;
        }

        return false;
    }
}
