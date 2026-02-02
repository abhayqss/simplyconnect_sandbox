package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.entity.Employee;

/**
 * @author phomal
 * Created on 7/28/2017.
 */
public class MockEmployeeSupplier extends MemoizingEmployeeSupplier {
    private final Employee employee;

    public MockEmployeeSupplier(Employee employee) {
        this.employee = employee;
    }

    @Override
    public Employee get() {
        return employee;
    }

}
