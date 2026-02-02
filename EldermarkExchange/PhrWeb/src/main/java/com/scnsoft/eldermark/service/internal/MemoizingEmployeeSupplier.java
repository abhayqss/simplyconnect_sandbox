package com.scnsoft.eldermark.service.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.scnsoft.eldermark.entity.Employee;

/**
 * @author phomal
 * Created on 7/27/2017.
 */
abstract class MemoizingEmployeeSupplier implements EmployeeSupplier, Supplier<Employee> {

    private final Supplier<Employee> memoized = Suppliers.memoize(this);

    @Override
    public Employee getEmployee() {
        return memoized.get();
    }

}
