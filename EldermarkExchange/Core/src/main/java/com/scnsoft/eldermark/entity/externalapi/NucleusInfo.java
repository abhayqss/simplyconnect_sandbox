package com.scnsoft.eldermark.entity.externalapi;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;

import javax.persistence.*;

/**
 * @author phomal
 * Created on 2/13/2018.
 */
@Entity
public class NucleusInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nucleus_user_id", nullable = false, length = 50)
    private String nucleusUserId;

    @Column(name = "family_ctm_id")
    private Long familyCareTeamMemberId;

    @OneToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long residentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNucleusUserId() {
        return nucleusUserId;
    }

    public void setNucleusUserId(String nucleusUserId) {
        this.nucleusUserId = nucleusUserId;
    }

    public Long getFamilyCareTeamMemberId() {
        return familyCareTeamMemberId;
    }

    public void setFamilyCareTeamMemberId(Long familyCareTeamMemberId) {
        this.familyCareTeamMemberId = familyCareTeamMemberId;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }
}
