package com.scnsoft.eldermark.entity;


import javax.persistence.*;

@Entity
@Table(name = "EmployeeWithAssociatedAllOptOutResidents")
public class EmployeeWithAssociatedAllOptOutClients {

    @Id
    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
