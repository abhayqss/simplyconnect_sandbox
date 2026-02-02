package com.scnsoft.eldermark.facades.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
    private long employeeId;

    public EmployeeNotFoundException(long employeeId) {
        super("Employee #" + employeeId + " not found");
        this.employeeId = employeeId;
    }

    public long getEmployeeId() {
        return employeeId;
    }
}
