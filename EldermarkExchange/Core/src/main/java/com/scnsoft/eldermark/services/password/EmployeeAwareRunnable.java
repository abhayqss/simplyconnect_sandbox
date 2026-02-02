package com.scnsoft.eldermark.services.password;

/**
 * @author phomal
 * Created on 11/14/2017.
 */
public interface EmployeeAwareRunnable extends Runnable {

    Long getEmployeeId();
    void setEmployeeId(Long employeeId);

}
