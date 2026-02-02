package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.util.Date;

/**
 * @author phomal
 * Created on 5/3/2017.
 */
@Entity
@Table(name="Activity")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", updatable = false, insertable = false)
    private User patient;

    @Column(name = "patient_id")
    private Long patientId;

    /**
     * Employee - Care Team Member.
     * In case of EventActivity, this field represents a receiver of Event Notification. <br/>
     * In case of CallActivity or VideoActivity this field represents a caller / callee. <br/>
     * In case of InvitationActivity this field represents the invitee. <br/>
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}
