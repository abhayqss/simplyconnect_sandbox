package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EmployeeService extends SecurityAwareEntityService<EmployeeSecurityAwareEntity, Long>, ProjectingService<Long> {

    Employee getEmployeeById(Long id);

    Optional<Employee> findById(Long id);

    <P> List<P> findAll(Specification<Employee> specification, Class<P> projection);

    Long findEmployeeOrganizationId(Long id);

    Long findEmployeeCommunityId(Long employeeId);

    List<Employee> findAllById(Iterable<Long> ids);

    Employee getEmployeeThatCanLogin(String loginName, String loginCompanyId) throws UsernameNotFoundException;

    Employee getEmployeeThatCanLoginOrInactive(String loginName, String loginCompanyId) throws UsernameNotFoundException;

    Employee getActiveOrInactiveEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException;

    boolean existsSelfClientRecordInCommunity(Employee employee, Long communityId);

    Employee getPendingEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException;

    List<Employee> getEmployeeAndLinkedEmployees(Employee employee);

    boolean existsLoginInOrganization(String login, Long organizationId);

    List<Employee> findInCommunityWithRole(Long communityId, CareTeamRoleCode role);

    List<Employee> findInOrganizationWithRole(Long organizationId, CareTeamRoleCode role);

    Optional<Employee> findExternalEmployee(String loginName, EmployeeStatus... statuses);

    List<Employee> findAllExternalEmployees(Collection<String> loginNames, EmployeeStatus... statuses);

    Employee findActiveByLegacyIdAndLoginCompanyId(String legacyId, String loginCompanyId);

    void updateStatus(EmployeeStatus status, Iterable<Long> employeeIds, boolean isAutoStatusChanged);
}
