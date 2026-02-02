package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.dao.password.EmployeePasswordSecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author phomal
 * Created on 11/14/2017.
 */
@Component("ResetLoginCounterRunnable_Employee")
@Scope("prototype")
class ResetLoginCounterRunnable implements EmployeeAwareRunnable {
    private Long employeeId;

    private final EmployeePasswordSecurityDao employeePasswordSecurityDao;

    @Autowired
    public ResetLoginCounterRunnable(EmployeePasswordSecurityDao employeePasswordSecurityDao) {
        this.employeePasswordSecurityDao = employeePasswordSecurityDao;
    }

    @Override
    @Transactional
    public void run() {
        employeePasswordSecurityDao.resetFailAttempts(employeeId);
    }

    @Override
    public Long getEmployeeId() {
        return employeeId;
    }

    @Override
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
