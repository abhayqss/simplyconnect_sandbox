package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.AssociatedClientIdsAware;
import com.scnsoft.eldermark.beans.projection.CareTeamRoleCodeAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseChatVideoCallSecurityService extends BaseSecurityService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    protected boolean canAddEmployee(PermissionFilter filter,
                                     Long employeeId,
                                     Permission addContactAllExceptOptOutClient,
                                     Permission addContactIfAssociatedOrganizationExceptOptOutClient,
                                     Permission addContactIfFromPrimaryOrganizationExceptOptOutClient,
                                     Permission addContactIfFromAffiliatedOrganizationExceptOptOutClient,
                                     Permission addContactIfFromPrimaryCommunityExceptOptOutClient,
                                     Permission addContactIfFromAffiliatedCommunityExceptOptOutClient,
                                     Permission addContactIfCreatedBySelfExceptOptOutClient,
                                     Permission addContactIfShareCurrentRpCtmExceptOptOutClient,
                                     Permission addContactIfSelfContactRpCurrentClientCtmExceptOptOutClient,
                                     Permission addContactIfAccessibleClientAssociatedContact) {

        var employee = employeeService.findById(employeeId, ChatEmployeeSecurityAwareEntity.class);
        var employeePermissions = CareTeamRolePermissionMapping.getPermissions(employee.getCareTeamRoleCode());
        var addContactPermissions = Set.of(
                addContactAllExceptOptOutClient,
                addContactIfAssociatedOrganizationExceptOptOutClient,
                addContactIfFromPrimaryOrganizationExceptOptOutClient,
                addContactIfFromAffiliatedOrganizationExceptOptOutClient,
                addContactIfFromPrimaryCommunityExceptOptOutClient,
                addContactIfFromAffiliatedCommunityExceptOptOutClient,
                addContactIfCreatedBySelfExceptOptOutClient,
                addContactIfShareCurrentRpCtmExceptOptOutClient,
                addContactIfSelfContactRpCurrentClientCtmExceptOptOutClient,
                addContactIfAccessibleClientAssociatedContact
        );

        if (employeePermissions.stream().noneMatch(addContactPermissions::contains)) {
            return false;
        }

        if (hasNoAssociatedClientsOrAnyAssociatedClientOptedIn(employee)) {
            if (filter.hasPermission(addContactAllExceptOptOutClient)) {
                return true;
            }

            if (filter.hasPermission(addContactIfAssociatedOrganizationExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfAssociatedOrganizationExceptOptOutClient);
                if (isAnyCreatedUnderOrganization(employees, employee.getOrganizationId())) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfFromPrimaryOrganizationExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfFromPrimaryOrganizationExceptOptOutClient);

                if (isAnyInPrimaryOrganizationOfCommunity(employees, employee.getCommunityId())) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfFromAffiliatedOrganizationExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfFromAffiliatedOrganizationExceptOptOutClient);

                if (isAnyInAffiliatedOrganizationOfCommunity(employees, employee.getCommunityId())) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfFromPrimaryCommunityExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfFromPrimaryCommunityExceptOptOutClient);

                if (isAnyInPrimaryCommunity(employees, employee.getCommunityId())) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfFromAffiliatedCommunityExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfFromAffiliatedCommunityExceptOptOutClient);

                if (isAnyInAffiliatedCommunity(employees, employee.getCommunityId())) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfCreatedBySelfExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfCreatedBySelfExceptOptOutClient);
                if (findEmployeeCreatedBySelf(employees, employee).isPresent()) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfShareCurrentRpCtmExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfShareCurrentRpCtmExceptOptOutClient);
                if (careTeamMemberService.doesAnyShareCtmWithEmployee(CareCoordinationUtils.toIdsSet(employees), employeeId,
                        HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                    return true;
                }
            }

            if (filter.hasPermission(addContactIfSelfContactRpCurrentClientCtmExceptOptOutClient)) {
                var employees = filter.getEmployees(addContactIfSelfContactRpCurrentClientCtmExceptOptOutClient);
                var associatedClientIdsOfRequestingEmployees = employees.stream().map(Employee::getAssociatedClientIds)
                        .flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toSet());
                if (clientCareTeamMemberService.isAnyEmployeeIdInAnyClientCareTeam(
                        Collections.singletonList(employee.getId()),
                        associatedClientIdsOfRequestingEmployees, AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                        HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                    return true;
                }
            }
        }

        if (filter.hasPermission(addContactIfAccessibleClientAssociatedContact)) {
            var employees = filter.getEmployees(addContactIfAccessibleClientAssociatedContact);
            var associatedClientIds = employee.getAssociatedClientIds();
            var adjustedFilterLazy = Lazy.of(() -> PermissionFilterUtils.filterWithEmployeesOnly(filter, employees));
            if (associatedClientIds.stream().anyMatch(client -> clientSecurityService.canView(client, adjustedFilterLazy.get()))) {
                return true;
            }
        }

        return false;
    }

    private interface ChatEmployeeSecurityAwareEntity extends EmployeeSecurityAwareEntity, AssociatedClientIdsAware, CareTeamRoleCodeAware {
    }
}
