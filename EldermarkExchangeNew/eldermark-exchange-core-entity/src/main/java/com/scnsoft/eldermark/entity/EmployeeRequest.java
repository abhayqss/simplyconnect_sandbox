package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "EmployeeRequest")
public class EmployeeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "created_date_time", nullable = false)
    private Instant createdDateTime;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRequestType tokenType;

    /**
     * Request creator (in case the target Employee has been invited by an
     * Employee). May be null.
     */
    @JoinColumn(name = "created_employee_id", referencedColumnName = "id")
    @ManyToOne
    private Employee createdEmployee;

    /**
     * Request creator (in case the target Employee has been invited by a Client).
     * May be null.
     */
    @JoinColumn(name = "created_resident_id", referencedColumnName = "id")
    @ManyToOne
    private Client createdClient;

    /**
     * The Employee who has been invited or whose password has been reset.
     */
    @JoinColumn(name = "target_employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee targetEmployee;

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

    public Instant getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Instant createdDateTime) {
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
 
    public Client getCreatedClient() {
        return createdClient;
    }

    public void setCreatedClient(Client createdClient) {
        this.createdClient = createdClient;
    }

    public Employee getTargetEmployee() {
        return targetEmployee;
    }

    public void setTargetEmployee(Employee targetEmployee) {
        this.targetEmployee = targetEmployee;
    }

}