package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 30-Oct-15.
 */
@Entity
@Table(name = "EmployeeRequest")
public class EmployeeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic(optional = false)
    @Column(name = "token")
    private String token;

    @Basic(optional = false)
    @Column(name = "created_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDateTime;

    @Basic(optional = false)
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private EmployeeRequestType tokenType;


    /**
     * Request creator (in case the target Employee has been invited by an Employee). May be null.
     */
    @JoinColumn(name = "created_employee_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Employee createdEmployee;

    /**
     * Request creator (in case the target Employee has been invited by a Resident). May be null.
     */
    @JoinColumn(name = "created_resident_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Resident createdResident;

    /**
     * The Employee who has been invited or whose password has been reset.
     */
    @JoinColumn(name = "target_employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee targetEmployee;

    public EmployeeRequest() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public EmployeeRequestType getTokenType() {
        return tokenType;
    }

    public void setTokenType(EmployeeRequestType tokenType) {
        this.tokenType = tokenType;
    }

    public Employee getCreatedEmployee() {
        return createdEmployee;
    }

    public void setCreatedEmployee(Employee createdEmployee) {
        this.createdEmployee = createdEmployee;
    }

    public Resident getCreatedResident() {
        return createdResident;
    }

    public void setCreatedResident(Resident createdResident) {
        this.createdResident = createdResident;
    }

    public Employee getTargetEmployee() {
        return targetEmployee;
    }

    public void setTargetEmployee(Employee targetEmployee) {
        this.targetEmployee = targetEmployee;
    }


    @Override
    public String toString() {
        return "EmployeeRequest{" +
                " token='" + token + '\'' +
                ", createdDateTime=" + createdDateTime +
                ", tokenType=" + tokenType +
                ", createdEmployee=" + createdEmployee +
                ", createdResident=" + createdResident +
                ", targetEmployee=" + targetEmployee +
                '}';
    }
}
