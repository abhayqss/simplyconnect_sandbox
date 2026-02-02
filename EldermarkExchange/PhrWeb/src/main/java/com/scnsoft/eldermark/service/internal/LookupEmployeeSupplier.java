package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Search employees in databases that correspond to the specified organizations.<br/>
 * If there're multiple occurrences, {@code getEmployee()} returns any of them.
 * <br/><br/>
 * A bean prototype that is instantiated and initialized by {@link EmployeeSupplierFactory#getEmployeeSupplier(Collection, String, String, String, String)}.
 *
 * @author phomal
 * Created on 7/27/2017.
 */
class LookupEmployeeSupplier extends MemoizingEmployeeSupplier {

    private final List<Long> organizationIds;
    private final String login;
    private final String phone;
    private final String firstName;
    private final String lastName;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrganizationDao organizationDao;

    public LookupEmployeeSupplier(Collection<Long> organizationIds, String login, String phone, String firstName, String lastName) {
        if (login == null || phone == null) {
            throw new IllegalStateException("not initialized");
        }

        this.organizationIds = (organizationIds == null) ? Collections.<Long>emptyList() : new ArrayList<>(organizationIds);
        this.login = login;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Employee get() {
        List<Long> databaseIds = organizationDao.getDatabasesByOrganizationIds(organizationIds);
        List<Employee> employees = employeeService.getEmployeesByData(databaseIds, Normalizer.normalizeEmail(login), Normalizer.normalizePhone(phone),
                firstName, lastName, null);
        if (CollectionUtils.isEmpty(employees)) {
            return null;
        } else {
            return employees.get(0);
        }
    }

}
