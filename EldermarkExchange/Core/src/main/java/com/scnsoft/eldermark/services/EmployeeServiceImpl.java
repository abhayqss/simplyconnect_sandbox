package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.carecoordination.OrganizationCareTeamMemberDao;
import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.services.password.PasswordHistoryService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Date: 19.05.15
 * Time: 11:15
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private static final StandardPasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DatabasesDao databasesDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    PasswordHistoryService passwordHistoryService;

    @Autowired
    EmployeePasswordSecurityService employeePasswordSecurityService;


    @Override
    @Transactional(readOnly = true)
    public Employee getActiveEmployee(String loginName, String loginCompanyId) throws UsernameNotFoundException {
        try {
            final Employee activeEmployee = employeeDao.getActiveEmployee(loginName, loginCompanyId);
            if (activeEmployee == null) {
                throw new UsernameNotFoundException(String.format("No users found [%s / %s]", loginName, loginCompanyId));
            }
            return activeEmployee;
        } catch (MultipleEntitiesFoundException e) {
            throw new UsernameNotFoundException(String.format("Duplicated (login, database) pair [%s / %s]", loginName, loginCompanyId));
        } catch (DataAccessException e) {
            throw new UsernameNotFoundException("Data access error", e);
        }
    }

    @Override
    public List<Employee> getEmployeesByData(List<Long> databaseIds, String login, String phone, String firstName, String lastName, Pageable pageable) {
        return employeeDao.getEmployeesByData(databaseIds, login, phone, firstName, lastName, pageable);
    }

    @Override
    public List<Employee> getEmployeesByData(String login, String phone, String firstName, String lastName) {
        return employeeDao.getEmployeesByData(null, login, phone, firstName, lastName, null);
    }

    @Override
    public Employee getEmployeeByLogin(long databaseId, String login) {
        return employeeDao.getEmployeeByLogin(databaseId, login);
    }

    public Employee getEmployee(long employeeId) {
        return employeeDao.getEmployee(employeeId);
    }

    public Pair<String, String> getCompanyLogosForEmployee(long employeeId, long databaseId, Set<Long> employeeAndLinkedEmployeeIds) {
        String mainLogoPath = null;
        String additionalLogoPath = null;

        List<Long> careTeamOrganizationIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(employeeAndLinkedEmployeeIds, databaseId);
        if (careTeamOrganizationIds.size() != 0) {
            Pair<String, String> logos = organizationDao.getOrganizationLogos(careTeamOrganizationIds.get(0));
            mainLogoPath = logos.getFirst();
            additionalLogoPath = logos.getSecond();
        } else {
            List<Organization> orgs = organizationDao.getOrganizationsByEmployee(employeeId);
            if (orgs.size() != 0) {
                mainLogoPath = orgs.get(0).getMainLogoPath();
                additionalLogoPath = orgs.get(0).getAdditionalLogoPath();
            }
        }
        Database database = databasesDao.getDatabaseById(databaseId);
        logger.info("EmployeeServiceImpl.getCompanyLogosForEmployee database:" + databaseId + " " + database);
        if (database != null) {
            mainLogoPath = database.getMainLogoPath() != null ? database.getMainLogoPath() : mainLogoPath;
            additionalLogoPath = database.getAdditionalLogoPath() != null ? database.getAdditionalLogoPath() : additionalLogoPath;
        }

        return new Pair<String, String>(mainLogoPath, additionalLogoPath);
    }

    @Override
    public List<Employee> getEmployees(long databaseId) {
        return employeeDao.getEmployeeForDatabase(databaseId);
    }

    @Override
    @Transactional
    public void updatePassword(long employeeId, String oldPassword, String newPassword) {
        Employee employee = employeeDao.get(employeeId);
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeDao.merge(employee);
        employeeDao.flush();
        passwordHistoryService.addCurrentPasswordToHistoryIfEnabled(employee);
        employeePasswordSecurityService.updatePasswordChangedTimeIfEnabled(employee);
    }
}
