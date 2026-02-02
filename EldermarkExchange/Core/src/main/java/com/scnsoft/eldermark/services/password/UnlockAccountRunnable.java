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
@Component("UnlockAccountRunnable_Employee")
@Scope("prototype")
class UnlockAccountRunnable implements EmployeeAwareRunnable {
    private Long employeeId;

    private final EmployeePasswordSecurityDao employeePasswordSecurityDao;

    @Autowired
    public UnlockAccountRunnable(EmployeePasswordSecurityDao employeePasswordSecurityDao) {
        this.employeePasswordSecurityDao = employeePasswordSecurityDao;
    }

    @Override
    @Transactional
    public void run() {
        employeePasswordSecurityDao.unlockEmployeeAccount(employeeId);
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
