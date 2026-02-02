package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.CareTeamRoleCodeAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIsAppointmentsEnabledAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("clientAppointmentSecurityService")
@Transactional(readOnly = true)
public class ClientAppointmentSecurityServiceImpl extends BaseSecurityService implements ClientAppointmentSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            APPOINTMENT_VIEW_LIST_ALL,
            APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_ORGANIZATION,
            APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_ORGANIZATION,
            APPOINTMENT_VIEW_LIST_IF_ASSOCIATED_COMMUNITY,
            APPOINTMENT_VIEW_LIST_IF_FROM_AFFILIATED_COMMUNITY,
            APPOINTMENT_VIEW_LIST_IF_CO_RP_COMMUNITY_CTM,
            APPOINTMENT_VIEW_LIST_IF_CO_RP_CLIENT_CTM,
            APPOINTMENT_VIEW_LIST_IF_SELF_CLIENT_RECORD,
            APPOINTMENT_VIEW_LIST_IF_ADDED_BY_SELF);

    private static final Set<Permission> ADD_PERMISSIONS = EnumSet.of(
            APPOINTMENT_ADD_ALL_EXCEPT_OPTED_OUT,
            APPOINTMENT_ADD_IF_ASSOCIATED_ORGANIZATION,
            APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            APPOINTMENT_ADD_IF_ASSOCIATED_COMMUNITY,
            APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            APPOINTMENT_ADD_IF_CURRENT_RP_COMMUNITY_CTM,
            APPOINTMENT_ADD_IF_CURRENT_RP_CLIENT_CTM,
            APPOINTMENT_ADD_IF_SELF_CLIENT_RECORD);

    private static final Set<Permission> CAN_COMPLETE_ROLES = EnumSet.of(
            ROLE_SUPER_ADMINISTRATOR,
            ROLE_ORGANIZATION_ADMINISTRATOR,
            ROLE_COMMUNITY_ADMINISTRATOR,
            ROLE_NURSE,
            ROLE_TELE_HEALTH_NURSE,
            ROLE_PERSON_RECEIVING_SERVICES,
            ROLE_PARENT_GUARDIAN,
            ROLE_CASE_MANAGER,
            ROLE_CARE_COORDINATOR);

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public boolean canViewList() {
        var permissionFilter = currentUserFilter();
        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }
        return hasAnyPermission(VIEW_LIST_PERMISSIONS) && organizationService.existsAccessibleOrganizationsWithAppointmentsEnabled(permissionFilter);
    }

    @Override
    public boolean canAdd(ClientAppointmentSecurityFieldsAware appointmentSecurityFieldsAware) {
        var permissionFilter = currentUserFilter();

        var client = lazyClient(appointmentSecurityFieldsAware.getClientId());

        if (permissionFilter.hasPermission(Permission.APPOINTMENT_ADD_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.get().getCommunityId()) && isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, client.get().getCommunityId()) && isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    client.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_ADD_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_ADD_IF_SELF_CLIENT_RECORD);
            if (isSelfClientRecord(employees, client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canView(Long appointmentId) {
        var permissionFilter = currentUserFilter();

        var appointmentSecurityFieldsAware = clientAppointmentService.findSecurityAwareEntity(appointmentId);
        var client = lazyClient(appointmentSecurityFieldsAware.getClientId());

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        var creator = employeeService.findById(appointmentSecurityFieldsAware.getCreatorId(), ClientAppointmentEmployeeSecurityAwareEntity.class);
        //disable view of appointments of clients of same organization created by users from different organization (affiliated + super admin)
        if (isAnyCreatedUnderOrganization(permissionFilter.getEmployees(), client.get().getOrganizationId())
                && !isAnyCreatedUnderOrganization(permissionFilter.getEmployees(), creator.getOrganizationId())) {
            return false;
        }

        if (BooleanUtils.isFalse(appointmentSecurityFieldsAware.getIsPublic())) {
            if (!isAnyAppointmentCreatorOrServiceProvider(permissionFilter.getEmployees(), appointmentSecurityFieldsAware)
                    && !isSelfClientRecord(permissionFilter.getEmployees(), appointmentSecurityFieldsAware.getClientId())) {
                return false;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.get().getCommunityId()) &&
                    isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, client.get().getCommunityId()) &&
                    isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_CURRENT_REGULAR_COMMUNITY_CTM_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_CURRENT_REGULAR_COMMUNITY_CTM_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION);
            if (isAnyInCommunityCareTeam(employees, client.get().getCommunityId(), AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client.get()))
                    && isCreatedByContactFromOrganizations(CareCoordinationUtils.getOrganizationIdsSet(employees), appointmentSecurityFieldsAware)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_CURRENT_PRIMARY_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_CURRENT_PRIMARY_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(employees, client.get().getCommunityId(), AffiliatedCareTeamType.PRIMARY, HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_CURRENT_REGULAR_CLIENT_CTM_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_CURRENT_REGULAR_CLIENT_CTM_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION);
            if (isAnyInClientCareTeam(employees, appointmentSecurityFieldsAware.getClientId(), AffiliatedCareTeamType.REGULAR, HieConsentCareTeamType.currentWithOptimizations(client.get()))
                    && isCreatedByContactFromOrganizations(CareCoordinationUtils.getOrganizationIdsSet(employees), appointmentSecurityFieldsAware)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_CURRENT_PRIMARY_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_CURRENT_PRIMARY_CLIENT_CTM);
            if (isAnyInClientCareTeam(employees, appointmentSecurityFieldsAware.getClientId(), AffiliatedCareTeamType.PRIMARY, HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_SELF_CLIENT_RECORD);
            if (isSelfClientRecord(employees, appointmentSecurityFieldsAware.getClientId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_VIEW_ITEM_IF_ADDED_BY_SELF_AND_CLIENT_OPTED_IN)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_VIEW_ITEM_IF_ADDED_BY_SELF_AND_CLIENT_OPTED_IN);
            if (isAddedBySelf(employees, appointmentSecurityFieldsAware) && isClientOptedIn(client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canEdit(Long appointmentId) {
        var permissionFilter = currentUserFilter();

        var appointmentSecurityFieldsAware = clientAppointmentService.findSecurityAwareEntity(appointmentId);
        var client = lazyClient(appointmentSecurityFieldsAware.getClientId());

        if (permissionFilter.hasPermission(Permission.APPOINTMENT_EDIT_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(client.get())) {
            return true;
        }

        if (BooleanUtils.isFalse(appointmentSecurityFieldsAware.getIsPublic())) {
            if (!isAnyAppointmentCreatorOrServiceProvider(permissionFilter.getEmployees(), appointmentSecurityFieldsAware)) {
                return false;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, client.get().getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION_EXCEPT_PRS_PG)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION_CREATED_BY_STAFF_FROM_ASSOCIATED_ORGANIZATION_EXCEPT_PRS_PG);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, client.get().getCommunityId())
                    && isCreatedByContactFromOrganizationsExceptPrsPg(CareCoordinationUtils.getOrganizationIdsSet(employees), appointmentSecurityFieldsAware)
                    && isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, client.get().getCommunityId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY_CREATED_BY_STAFF_FROM_ASSOCIATED_COMMUNITY_EXCEPT_PRS_PG)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY_CREATED_BY_STAFF_FROM_ASSOCIATED_COMMUNITY_EXCEPT_PRS_PG);
            if (isAnyInAffiliatedCommunity(employees, client.get().getCommunityId())
                    && isCreatedByContactFromCommunityExceptPrsPg(CareCoordinationUtils.getCommunityIdsSet(employees), appointmentSecurityFieldsAware)
                    && isClientOptedIn(client.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    client.get().getCommunityId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInClientCareTeam(
                    employees,
                    appointmentSecurityFieldsAware.getClientId(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentWithOptimizations(client.get()))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(APPOINTMENT_EDIT_IF_ADDED_BY_SELF_AND_CLIENT_OPTED_IN)) {
            var employees = permissionFilter.getEmployees(APPOINTMENT_EDIT_IF_ADDED_BY_SELF_AND_CLIENT_OPTED_IN);
            if (isAddedBySelf(employees, appointmentSecurityFieldsAware) && isClientOptedIn(client.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canAddInOrganization(Long organizationId) {
        var organization = organizationService.findById(organizationId, OrganizationIsAppointmentsEnabledAware.class);
        return BooleanUtils.isTrue(organization.getIsAppointmentsEnabled()) && hasAnyPermission(ADD_PERMISSIONS);
    }

    @Override
    public boolean canComplete(Long appointmentId) {
        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (!canEdit(appointmentId)) {
            return false;
        }

        if (permissionFilter.hasAnyPermission(CAN_COMPLETE_ROLES)) {
            return true;
        }

        var appointment = clientAppointmentService.findSecurityAwareEntity(appointmentId);
        return permissionFilter.getEmployees().stream().anyMatch(employee -> appointment.getCreatorId().equals(employee.getId()));
    }

    private boolean isCreatedByContactFromCommunityExceptPrsPg(Set<Long> communityIdsSet, ClientAppointmentSecurityFieldsAware appointmentSecurityFieldsAware) {
        var creator = employeeService.findById(appointmentSecurityFieldsAware.getCreatorId(), ClientAppointmentEmployeeSecurityAwareEntity.class);
        return creator.getCareTeamRoleCode() != CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES
                && creator.getCareTeamRoleCode() != CareTeamRoleCode.ROLE_PARENT_GUARDIAN
                && communityIdsSet.stream().anyMatch(communityId -> creator.getCommunityId().equals(communityId));
    }

    private boolean isCreatedByContactFromOrganizationsExceptPrsPg(Set<Long> organizationIdsSet, ClientAppointmentSecurityFieldsAware appointmentSecurityFieldsAware) {
        var creator = employeeService.findById(appointmentSecurityFieldsAware.getCreatorId(), ClientAppointmentEmployeeSecurityAwareEntity.class);
        return creator.getCareTeamRoleCode() != CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES
                && creator.getCareTeamRoleCode() != CareTeamRoleCode.ROLE_PARENT_GUARDIAN
                && organizationIdsSet.stream().anyMatch(organizationId -> creator.getOrganizationId().equals(organizationId));
    }

    private boolean isCreatedByContactFromOrganizations(Set<Long> organizationIdsSet, ClientAppointmentSecurityFieldsAware appointmentSecurityFieldsAware) {
        var creator = employeeService.findById(appointmentSecurityFieldsAware.getCreatorId(), ClientAppointmentEmployeeSecurityAwareEntity.class);
        return organizationIdsSet.stream().anyMatch(organizationId -> creator.getOrganizationId().equals(organizationId));
    }

    private boolean isAddedBySelf(List<Employee> employees, ClientAppointmentSecurityFieldsAware appointment) {
        return employees.stream().anyMatch(employee -> appointment.getCreatorId().equals(employee.getId()));
    }

    private boolean isAnyAppointmentCreatorOrServiceProvider(List<Employee> employees, ClientAppointmentSecurityFieldsAware appointment) {
        return employees.stream().anyMatch(employee -> appointment.getCreatorId().equals(employee.getId()) || appointment.getServiceProviderIds().contains(employee.getId()));
    }

    private interface ClientAppointmentEmployeeSecurityAwareEntity extends EmployeeSecurityAwareEntity, CareTeamRoleCodeAware {
    }
}
