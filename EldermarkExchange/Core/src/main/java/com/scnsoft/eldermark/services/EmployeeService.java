package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

/**
 * @author aduzhynskaya
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Date: 19.05.15
 * Time: 11:15
 */
public interface EmployeeService {

    /**
     * Finds active employee by his login name and system_setup.login_company_id
     *
     * @return employee with corresponding login name
     * @throws UsernameNotFoundException in case of no employee found or multiple employees with duplicated logins exist or data access error occurred
     * @see com.scnsoft.eldermark.dao.EmployeeDao#getActiveEmployee(String, String)
     */
    Employee getActiveEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException;
    List<Employee> getEmployeesByData(List<Long> databaseIds, String login, String phone, String firstName, String lastName, Pageable pageable);
    List<Employee> getEmployeesByData(String login, String phone, String firstName, String lastName);

    /**
     * Finds employee by his login name and database ID.
     *
     * @return employee with corresponding login name or null if employee can not be found
     * @throws com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException
     *          if more than one employee has been found (this is not a normal case)
     */
    Employee getEmployeeByLogin(long databaseId, String login);

    Employee getEmployee(long employeeId);
    Pair<String, String> getCompanyLogosForEmployee(long employeeId, long databaseId, Set<Long> employeeAndLinkedEmployeeIds);
    List<Employee> getEmployees(long databaseId);
    void updatePassword(long employeeId, String oldPassword, String newPassword);

}
