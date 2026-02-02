package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.entity.Employee;

/**
 * @author phomal
 * Created on 7/27/2017.
 */
public interface EmployeeSupplier {

    /**
     * Retrieves an instance of Employee.
     * The returned object instance is memoized, so the method returns the same value on subsequent calls.
     *
     * @return Employee
     */
    Employee getEmployee();

}
