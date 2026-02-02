package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.SimpleEmployee;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeDao extends BaseDao<Employee> {
    /**
     * Finds active employee by his login name and system_setup.login_company_id.
     *
     * @param loginName  employee's login name (can be null)
     * @param loginCompanyId system_setup.login_company_id (can be null)
     * @return employee with corresponding login name or null if employee can not be found
     * @throws com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException
     *          if more than one employee has been found (this is not a normal case)
     */
    Employee getActiveEmployee(String loginName, String loginCompanyId);

    Employee getEmployee(long employeeId);

    Employee getEmployee(long databaseId, String employeeLegacyId);

    /**
     * Finds employee by his login name and database ID.
     *
     * @param login employee's login name (can be null)
     * @param databaseId databaseId (can be null)
     * @return employee with corresponding login name or null if employee can not be found
     * @throws com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException
     *          if more than one employee has been found (this is not a normal case)
     */
    Employee getEmployeeByLogin(long databaseId, String login);

    boolean isEmployeeLoginTaken(long databaseId, String login);

    List<Employee> getEmployeeForDatabase(Long databaseId);

    List<SimpleEmployee> getEmployeeList(Long databaseId, Long communityId, String searchString);

    List<SimpleEmployee> getAffiliatedEmployeeList(Long organizationId, Long databaseId);

    boolean isSecureEmailExist(String secureEmail);

    Long getEmployeeIdBySecureEmail(String secureEmail);

    List<Employee> getEmployeeForDatabase(final Database database, final ContactFilterDto filter, final Pageable pageable, Long currentEmployeeId);
    Long getEmployeeForDatabaseCount(final Database database, final ContactFilterDto filter);

    Long getEmployeeCommunityId(long employeeId);

    List<Employee> getAdministrators(Long databaseId);

    List<Employee> getCommunityAdministrators(Long communityId);

    List<Employee> getEmployees(List<Long> ids);

    // Not used. Replaced with getEmployeesByData(List<Long>, String, String, String, String)
    List<Employee> getEmployeesByData(String emailNormalized, String phoneNormalized);

    List<Employee> getEmployeesByData(List<Long> databaseIds, String emailNormalized, String phoneNormalized, String firstName, String lastName, Pageable pageable);

//    List<Employee> getAffiliatedEmployeesForResident(Long residentId);
}
