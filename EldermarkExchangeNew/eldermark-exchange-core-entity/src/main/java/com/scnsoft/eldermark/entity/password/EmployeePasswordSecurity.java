package com.scnsoft.eldermark.entity.password;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "EmployeePasswordSecurity")
public class EmployeePasswordSecurity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "failed_logons")
    private Integer failedLogonsCount;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "locked_time" , nullable = true)
    private Instant lockedTime;

    @Column(name = "change_password_time")
    private Instant changePasswordTime;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFailedLogonsCount() {
        return failedLogonsCount;
    }

    public void setFailedLogonsCount(Integer failedLogonsCount) {
        this.failedLogonsCount = failedLogonsCount;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Instant getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(Instant lockedTime) {
        this.lockedTime = lockedTime;
    }

    public Instant getChangePasswordTime() {
        return changePasswordTime;
    }

    public void setChangePasswordTime(Instant changePasswordTime) {
        this.changePasswordTime = changePasswordTime;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
}
