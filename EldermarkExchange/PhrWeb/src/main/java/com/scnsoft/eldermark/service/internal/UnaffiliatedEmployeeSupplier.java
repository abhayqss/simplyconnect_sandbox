package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

/**
 * Search employee in "Unaffiliated" organization.
 * <br/>
 * A bean prototype that is instantiated and initialized by {@link EmployeeSupplierFactory#getUnaffiliatedEmployeeSupplier(String, String, String, String)}.
 *
 * @author phomal
 * Created on 7/27/2017.
 */
class UnaffiliatedEmployeeSupplier extends MemoizingEmployeeSupplier {

    private final String login;
    private final String phone;
    private final String firstName;
    private final String lastName;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DatabasesService databasesService;

    public UnaffiliatedEmployeeSupplier(String login, String phone, String firstName, String lastName) {
        if (login == null || phone == null) {
            throw new IllegalStateException("not initialized");
        }

        this.login = login;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UnaffiliatedEmployeeSupplier(String login) {
        if (login == null) {
            throw new IllegalStateException("not initialized");
        }

        this.login = login;
        this.phone = null;
        this.firstName = null;
        this.lastName = null;
    }

    @Override
    public Employee get() {
        Database unaffiliatedDatabase = databasesService.getUnaffiliatedDatabase();
        if (phone == null) {
            return employeeService.getEmployeeByLogin(unaffiliatedDatabase.getId(), login);
        }
        Pageable pageable = new PageRequest(0, 1);
        List<Employee> employees = employeeService.getEmployeesByData(Collections.singletonList(unaffiliatedDatabase.getId()),
                Normalizer.normalizeEmail(login), Normalizer.normalizePhone(phone), firstName, lastName, pageable);
        if (CollectionUtils.isEmpty(employees)) {
            return null;
        } else {
            return employees.get(0);
        }
    }

}
