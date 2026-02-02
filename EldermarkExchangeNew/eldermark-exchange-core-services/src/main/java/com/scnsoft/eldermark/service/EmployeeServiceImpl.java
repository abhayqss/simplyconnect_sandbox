package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;


    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeDao.getOne(id);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeDao.findById(id);
    }

    @Override
    public <P> List<P> findAll(Specification<Employee> specification, Class<P> projection) {
        return employeeDao.findAll(specification, projection);
    }

    @Override
    public Long findEmployeeOrganizationId(Long id) {
        return employeeDao.findEmployeeOrganizationId(id);
    }

    @Override
    public Long findEmployeeCommunityId(Long employeeId) {
        return employeeDao.findEmployeeCommunityId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllById(Iterable<Long> ids) {
        return employeeDao.findAllById(ids);
    }

    @Override
    public Employee getEmployeeThatCanLogin(String loginName, String loginCompanyId) throws UsernameNotFoundException {
        return getEmployee(loginName, loginCompanyId, EmployeeStatus.allCanLogin());
    }

    @Override
    public Employee getEmployeeThatCanLoginOrInactive(String loginName, String loginCompanyId) throws UsernameNotFoundException {
        var statuses = Arrays.stream(EmployeeStatus.values()).filter(EmployeeStatus::canLogin).collect(Collectors.toList());
        statuses.add(EmployeeStatus.INACTIVE);
        return getEmployee(loginName, loginCompanyId, statuses.toArray(new EmployeeStatus[0]));
    }

    @Override
    public Employee getActiveOrInactiveEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException {
        return getEmployee(loginName, loginCompanyId, EmployeeStatus.ACTIVE, EmployeeStatus.INACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeeAndLinkedEmployees(Employee employee) {
        // todo implement in phase 2
        return Collections.singletonList(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsSelfClientRecordInCommunity(Employee employee, Long communityId) {
        return employeeDao.existsByIdAndAssociatedClientsCommunityId(employee.getId(), communityId);
    }

    @Override
    public Employee getPendingEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException {
        return getEmployee(loginName, loginCompanyId, EmployeeStatus.PENDING);
    }

    @Override
    public boolean existsLoginInOrganization(String login, Long organizationId) {
        return employeeDao.existsByLoginNameAndOrganizationIdAndStatusNot(login, organizationId, EmployeeStatus.DECLINED);
    }

    @Override
    public List<Employee> findInCommunityWithRole(Long communityId, CareTeamRoleCode role) {
        var byCommunity = employeeSpecificationGenerator.byCommunityId(communityId);
        var bySystemRole = employeeSpecificationGenerator.bySystemRole(role);
        var active = employeeSpecificationGenerator.active();

        return employeeDao.findAll(byCommunity.and(bySystemRole).and(active));
    }

    @Override
    public List<Employee> findInOrganizationWithRole(Long organizationId, CareTeamRoleCode role) {
        var byOrganizationId = employeeSpecificationGenerator.byOrganizationId(organizationId);
        var bySystemRole = employeeSpecificationGenerator.bySystemRole(role);
        var active = employeeSpecificationGenerator.active();

        return employeeDao.findAll(byOrganizationId.and(bySystemRole).and(active));
    }

    private Employee getEmployee(String loginName, String loginCompanyId, EmployeeStatus... statuses) {
        List<Employee> employees = employeeDao.findByLoginNameAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(
                loginName, loginCompanyId, statuses);
        if (CollectionUtils.isEmpty(employees)) {
            throw new UsernameNotFoundException(String.format("No users found [%s / %s]", loginName, loginCompanyId));
        }
        if (employees.size() > 1) {
            throw new UsernameNotFoundException(
                    String.format("Duplicated (login, database) pair [%s / %s]", loginName, loginCompanyId));
        }
        return employees.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findExternalEmployee(String loginName, EmployeeStatus... statuses) {
        return employeeDao.findFirstByLoginNameAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(loginName, CareCoordinationConstants.EXTERNAL_COMPANY_ID, statuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAllExternalEmployees(Collection<String> loginNames, EmployeeStatus... statuses) {
        return employeeDao.findAllByLoginNameInAndOrganization_SystemSetup_LoginCompanyIdAndStatusIn(loginNames, CareCoordinationConstants.EXTERNAL_COMPANY_ID, statuses);

    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return employeeDao.findById(id, EmployeeSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return employeeDao.findByIdIn(ids, EmployeeSecurityAwareEntity.class);

    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return employeeDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return employeeDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findActiveByLegacyIdAndLoginCompanyId(String legacyId, String loginCompanyId) {
        var active = employeeSpecificationGenerator.active();
        var byLegacyId = employeeSpecificationGenerator.byLegacyId(legacyId);
        var byLoginCompanyId = employeeSpecificationGenerator.byLoginCompanyId(loginCompanyId);
        List<Employee> employees = employeeDao.findAll(active.and(byLegacyId).and(byLoginCompanyId));
        if (CollectionUtils.isEmpty(employees)) {
            throw new UsernameNotFoundException(String.format("No users found [%s / %s]", legacyId, loginCompanyId));
        }
        if (employees.size() > 1) {
            throw new UsernameNotFoundException(
                    String.format("Duplicated (legacyId, loginCompanyId) pair [%s / %s]", legacyId, loginCompanyId));
        }
        return employees.get(0);
    }

    @Override
    @Transactional
    public void updateStatus(EmployeeStatus status, Iterable<Long> employeeIds, boolean isAutoStatusChanged) {
        Instant deactivateDatetime = null;
        if (EmployeeStatus.INACTIVE == status && isAutoStatusChanged) {
            deactivateDatetime = Instant.now();
        }
        employeeDao.updateStatus(status, employeeIds, isAutoStatusChanged, deactivateDatetime);
    }

}
