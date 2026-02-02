package com.scnsoft.eldermark.entity.client.insurance;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentInsuranceAuthorization")
public class ClientInsuranceAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "resident_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @JoinColumn(name = "created_by_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee createdBy;

    @Column(name = "created_by_id", nullable = false, insertable = false, updatable = false)
    private Long createdById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
}
