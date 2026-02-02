package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientExpenseSecurityAwareEnity;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityExpenseFieldsAware;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.client.expense.ClientExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientExpenseSecurityService")
@Transactional(readOnly = true)
public class ClientExpenseSecurityServiceImpl extends BaseSecurityService implements ClientExpenseSecurityService {

    @Autowired
    private ClientExpenseService clientExpenseService;

    @Override
    public boolean canAdd(Long clientId) {
        var client = clientService.findById(clientId, ClientSecurityExpenseFieldsAware.class);

        if (!Boolean.TRUE.equals(client.getActive())) {
            return false;
        }

        return hasPermissions(
                client,
                CLIENT_EXPENSE_ADD_ALL_EXCEPT_OPTED_OUT,
                CLIENT_EXPENSE_ADD_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_EXPENSE_ADD_IF_ASSOCIATED_COMMUNITY,
                null,
                null,
                null,
                CLIENT_EXPENSE_ADD_IF_CURRENT_REGULAR_COMMUNITY_CTM,
                null,
                CLIENT_EXPENSE_ADD_IF_CURRENT_REGULAR_CLIENT_CTM,
                CLIENT_EXPENSE_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                null
        );
    }

    @Override
    public boolean canViewList(Long clientId) {
        return hasPermissions(
                clientService.findById(clientId, ClientSecurityExpenseFieldsAware.class),
                CLIENT_EXPENSE_VIEW_ALL_EXCEPT_OPTED_OUT,
                CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_ORGANIZATION,
                CLIENT_EXPENSE_VIEW_IF_ASSOCIATED_COMMUNITY,
                CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
                CLIENT_EXPENSE_VIEW_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
                CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_COMMUNITY_CTM,
                null,
                CLIENT_EXPENSE_VIEW_IF_CURRENT_RP_CLIENT_CTM,
                null,
                CLIENT_EXPENSE_VIEW_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
                CLIENT_EXPENSE_VIEW_IF_SELF_RECORD
        );
    }

    @Override
    public boolean canView(Long expenseId) {
        var expense = clientExpenseService.findById(expenseId, ClientExpenseSecurityAwareEnity.class)
                .orElseThrow();

        return canViewList(expense.getClientId());
    }

    private boolean hasPermissions(
            ClientSecurityAwareEntity client,
            Permission allExceptOptedOutPermission,
            Permission ifAssociatedOrganizationPermission,
            Permission ifAssociatedCommunityPermission,
            Permission optedInIfFromAffiliatedOrganizationPermission,
            Permission optedInIfFromAffiliatedCommunityPermission,
            Permission ifCurrentRpCommunityCtmPermission,
            Permission ifCurrentRegularCommunityCtmPermission,
            Permission ifCurrentRpClientCtmPermission,
            Permission ifCurrentRegularClientCtmPermission,
            Permission ifOptedInClientAddedBySelfPermission,
            Permission ifSelfRecordPermission
    ) {

        var permissionFilter = currentUserFilter();

        if (!isEligibleForDiscoveryCommunity(client.getCommunityId())) {
            return false;
        }

        if (permissionFilter.hasPermission(allExceptOptedOutPermission) && isClientOptedIn(client)) {
            return true;
        }

        if (ifAssociatedOrganizationPermission != null
                && permissionFilter.hasPermission(ifAssociatedOrganizationPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedOrganizationPermission);
            if (isAnyCreatedUnderOrganization(employees, client.getOrganizationId())) {
                return true;
            }
        }

        if (ifAssociatedCommunityPermission != null
                && permissionFilter.hasPermission(ifAssociatedCommunityPermission)) {
            var employees = permissionFilter.getEmployees(ifAssociatedCommunityPermission);
            if (isAnyCreatedUnderCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (optedInIfFromAffiliatedOrganizationPermission != null &&
                permissionFilter.hasPermission(optedInIfFromAffiliatedOrganizationPermission) &&
                isClientOptedIn(client)
        ) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedOrganizationPermission);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (optedInIfFromAffiliatedCommunityPermission != null &&
                permissionFilter.hasPermission(optedInIfFromAffiliatedCommunityPermission) &&
                isClientOptedIn(client)
        ) {
            var employees = permissionFilter.getEmployees(optedInIfFromAffiliatedCommunityPermission);
            if (isAnyInAffiliatedCommunity(employees, client.getCommunityId())) {
                return true;
            }
        }

        if (ifCurrentRpCommunityCtmPermission != null &&
                permissionFilter.hasPermission(ifCurrentRpCommunityCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpCommunityCtmPermission);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (ifCurrentRegularCommunityCtmPermission != null &&
                permissionFilter.hasPermission(ifCurrentRegularCommunityCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRegularCommunityCtmPermission);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.getCommunityId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (ifCurrentRpClientCtmPermission != null &&
                permissionFilter.hasPermission(ifCurrentRpClientCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRpClientCtmPermission);
            if (isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (ifCurrentRegularClientCtmPermission != null &&
                permissionFilter.hasPermission(ifCurrentRegularClientCtmPermission)) {
            var employees = permissionFilter.getEmployees(ifCurrentRegularClientCtmPermission);
            if (isAnyInClientCareTeam(
                    employees,
                    client.getId(),
                    AffiliatedCareTeamType.REGULAR,
                    HieConsentCareTeamType.currentWithOptimizations(client))) {
                return true;
            }
        }

        if (ifOptedInClientAddedBySelfPermission != null &&
                permissionFilter.hasPermission(ifOptedInClientAddedBySelfPermission)) {
            var employees = permissionFilter.getEmployees(ifOptedInClientAddedBySelfPermission);
            if (isClientOptedInAndAddedBySelf(employees, client)) {
                return true;
            }
        }

        if (ifSelfRecordPermission != null &&
                permissionFilter.hasPermission(ifSelfRecordPermission)) {
            var employees = permissionFilter.getEmployees(ifSelfRecordPermission);
            if (isSelfClientRecord(employees, client)) {
                return true;
            }
        }

        return false;
    }
}
