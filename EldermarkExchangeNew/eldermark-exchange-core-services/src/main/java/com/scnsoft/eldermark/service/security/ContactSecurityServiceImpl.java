package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ContactSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("contactSecurityService")
@Transactional(readOnly = true)
public class ContactSecurityServiceImpl extends BaseSecurityService implements ContactSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(ContactSecurityServiceImpl.class);

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(ROLE_SUPER_ADMINISTRATOR,
            CONTACT_VIEW_IF_ASSOCIATED_ORGANIZATION,
            CONTACT_VIEW_IF_CREATED_BY_SELF,
            CONTACT_VIEW_IF_SELF_RECORD);

    private Long externalOrganizationId;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private OrganizationDao organizationDao;

    @PostConstruct
    private void setExternalOrganizationId() {
        externalOrganizationId = organizationDao
                .findBySystemSetup_LoginCompanyId(CareCoordinationConstants.EXTERNAL_COMPANY_ID, IdAware.class)
                .getId();
    }

    @Override
    public boolean canAddAnyRole(Long organizationId, Long communityId) {
        return canAdd(new ContactSecurityFieldsAware() {
            @Override
            public Long getSystemRoleId() {
                //in order to check that user can add contact in given organization and community at all
                return CareTeamRoleService.ANY_TARGET_ROLE;
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }

            @Override
            public Long getOrganizationId() {
                return organizationId;
            }
        });
    }

    @Override
    public boolean canAddAssociatedClientContact(Long organizationId, Long communityId) {
        return canAdd(new ContactSecurityFieldsAware() {
            @Override
            public Long getSystemRoleId() {
                return careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES).getId();
            }

            @Override
            public Long getCommunityId() {
                return communityId;
            }

            @Override
            public Long getOrganizationId() {
                return organizationId;
            }
        });
    }

    @Override
    public boolean canAdd(ContactSecurityFieldsAware dto) {
        var communityId = dto.getCommunityId();
        Long organizationId;
        try {
            organizationId = resolveAndValidateOrganizationId(communityId, dto.getOrganizationId(), ANY_TARGET_COMMUNITY);
        } catch (Exception e) {
            logger.warn("Failed to resolve organization", e);
            return false;
        }

        if (organizationId.equals(externalOrganizationId)) {
            return false;
        }

        if (!isEligibleForDiscovery(communityId, organizationId, ANY_TARGET_COMMUNITY)) {
            return false;
        }

        var permissionFilter = currentUserFilter();
        if (permissionFilter.hasPermission(CONTACT_ADD_BY_MATRIX_ALL)) {
            return true;
        }

        if (permissionFilter.hasPermission(CONTACT_ADD_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CONTACT_ADD_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION);
            var employeeInOrganization = findEmployeeInOrganization(employees, organizationId);
            if (employeeInOrganization.isPresent() && isEditableSystemRole(employeeInOrganization.get(), dto.getSystemRoleId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CONTACT_ADD_BY_MATRIX_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(CONTACT_ADD_BY_MATRIX_IF_ASSOCIATED_COMMUNITY);

            if (ANY_TARGET_COMMUNITY.equals(communityId)) {
                var employeeInOrganization = findEmployeeInOrganization(employees, organizationId);
                if (employeeInOrganization.isPresent() && isEditableSystemRole(employeeInOrganization.get(), dto.getSystemRoleId())) {
                    return true;
                }
            } else {
                var employeeInCommunity = findEmployeeInCommunity(employees, communityId);
                if (employeeInCommunity.isPresent() && isEditableSystemRole(employeeInCommunity.get(), dto.getSystemRoleId())) {
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean canEdit(Long employeeId, Long targetSystemRoleId) {
        var targetEmployeeSecurityAware = employeeService.findSecurityAwareEntity(employeeId);

        if (targetEmployeeSecurityAware.getCommunityId() != null && !isInEligibleForDiscoveryCommunity(targetEmployeeSecurityAware)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (targetEmployeeSecurityAware.getStatus() != EmployeeStatus.CONFIRMED) {
            if (permissionFilter.hasPermission(CONTACT_EDIT_BY_MATRIX_ALL)) {
                return true;
            }

            if (permissionFilter.hasPermission(CONTACT_EDIT_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION)) {
                var employees = permissionFilter.getEmployees(CONTACT_EDIT_BY_MATRIX_IF_ASSOCIATED_ORGANIZATION);
                var employeeInOrganization = findEmployeeInOrganization(employees, targetEmployeeSecurityAware.getOrganizationId());
                if (employeeInOrganization.isPresent() && isEditableSystemRole(employeeInOrganization.get(), targetEmployeeSecurityAware, targetSystemRoleId)) {
                    return true;
                }
            }

            if (permissionFilter.hasPermission(CONTACT_EDIT_BY_MATRIX_IF_ASSOCIATED_COMMUNITY)) {
                var employees = permissionFilter.getEmployees(CONTACT_EDIT_BY_MATRIX_IF_ASSOCIATED_COMMUNITY);
                if (targetEmployeeSecurityAware.getCommunityId() != null) {
                    var employeeInCommunity = findEmployeeInCommunity(employees, targetEmployeeSecurityAware.getCommunityId());
                    if (employeeInCommunity.isPresent() && isEditableSystemRole(employeeInCommunity.get(), targetEmployeeSecurityAware, targetSystemRoleId)) {
                        return true;
                    }
                }
            }

            if (permissionFilter.hasPermission(CONTACT_EDIT_BY_MATRIX_IF_CREATED_BY_SELF)) {
                var employees = permissionFilter.getEmployees(CONTACT_EDIT_BY_MATRIX_IF_CREATED_BY_SELF);
                var createdBySelf = findEmployeeCreatedBySelf(employees, targetEmployeeSecurityAware);
                if (createdBySelf.isPresent() && isEditableSystemRole(createdBySelf.get(), targetEmployeeSecurityAware, targetSystemRoleId)) {
                    return true;
                }
            }
        }

        if (permissionFilter.hasPermission(CONTACT_EDIT_BY_MATRIX_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(CONTACT_EDIT_BY_MATRIX_IF_SELF_RECORD);
            var selfEmployeeRecord = findSelfEmployeeRecord(employees, employeeId);
            if (selfEmployeeRecord.isPresent() && isEditableSystemRole(selfEmployeeRecord.get(), targetEmployeeSecurityAware, targetSystemRoleId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    public boolean canView(Long employeeId) {
        var employee = employeeService.findSecurityAwareEntity(employeeId);

        if (employee.getCommunityId() != null && !isInEligibleForDiscoveryCommunity(employee)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }

        if (permissionFilter.hasPermission(CONTACT_VIEW_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(CONTACT_VIEW_IF_ASSOCIATED_ORGANIZATION);
            if (isAnyCreatedUnderOrganization(employees, employee.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CONTACT_VIEW_IF_SELF_RECORD)) {
            var employees = permissionFilter.getEmployees(CONTACT_VIEW_IF_SELF_RECORD);
            if (isSelfEmployeeRecord(employees, employeeId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(CONTACT_VIEW_IF_CREATED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(CONTACT_VIEW_IF_CREATED_BY_SELF);
            if (findEmployeeCreatedBySelf(employees, employee).isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewDirectoryList(Long organizationId) {
        if (!organizationService.hasEligibleForDiscoveryCommunities(organizationId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();
        if (permissionFilter.hasPermission(ROLE_SUPER_ADMINISTRATOR)) {
            return true;
        }
        if (isAnyCreatedUnderOrganization(permissionFilter.getEmployees(), organizationId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canViewDirectoryList(Collection<Long> organizationIds) {
        return organizationIds.stream().allMatch(this::canViewDirectoryList);
    }

    @Override
    public boolean canEditRole(CareTeamRole targetContactRole) {
        return targetContactRole == null || targetContactRole.getCode() != CareTeamRoleCode.ROLE_EXTERNAL_USER;
    }

    private boolean isEditableSystemRole(Employee current, EmployeeSecurityAwareEntity target, Long targetSystemRoleId) {
        var currentRoleId = target.getCareTeamRoleId();
        return careTeamRoleService.isEditableContactRole(current.getCareTeamRole(), currentRoleId, targetSystemRoleId);
    }

    private boolean isEditableSystemRole(Employee current, Long targetCareTeamRoleId) {
        return careTeamRoleService.isEditableContactRole(current.getCareTeamRole(), null, targetCareTeamRoleId);
    }
}
