package com.scnsoft.eldermark.entity.password;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "EmployeePasswordSecurity")
public class EmployeePasswordSecurity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "failed_logons")
    private Integer failedLogonsCount;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "locked_time")
    private Date lockedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "change_password_time")
    private Date changePasswordTime;

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

    public Date getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(Date lockedTime) {
        this.lockedTime = lockedTime;
    }

    public Date getChangePasswordTime() {
        return changePasswordTime;
    }

    public void setChangePasswordTime(Date changePasswordTime) {
        this.changePasswordTime = changePasswordTime;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
