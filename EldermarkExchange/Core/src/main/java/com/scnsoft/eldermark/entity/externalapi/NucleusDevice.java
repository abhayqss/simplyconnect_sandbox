package com.scnsoft.eldermark.entity.externalapi;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;

import javax.persistence.*;

/**
 * @author phomal
 * Created on 2/13/2018.
 */
@Entity
public class NucleusDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nucleus_id", nullable = false, length = 50)
    private String nucleusId;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "type", length = 255)
    private String type;

    @OneToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNucleusId() {
        return nucleusId;
    }

    public void setNucleusId(String nucleusId) {
        this.nucleusId = nucleusId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
