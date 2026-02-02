package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="AuditLog")
public class AuditLog {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name="action", nullable = false)
    private AuditLogAction action;

    @Column(name = "remote_address", nullable = false)
    private String remoteAddress;

    @ManyToOne
    @JoinColumn(name="employee_id", nullable = false)
    private Employee employee;

    @ManyToMany
    @JoinTable(name="AuditLog_Residents",
               joinColumns = @JoinColumn(name="audit_log_id"),
               inverseJoinColumns = @JoinColumn(name="resident_id"))
    private List<Resident> residents;

    @ManyToMany
    @JoinTable(name="AuditLog_Documents",
            joinColumns = @JoinColumn(name="audit_log_id"),
            inverseJoinColumns = @JoinColumn(name="document_id"))
    private List<Document> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AuditLogAction getAction() {
        return action;
    }

    public void setAction(AuditLogAction action) {
        this.action = action;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}