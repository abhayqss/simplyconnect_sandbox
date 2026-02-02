package com.scnsoft.eldermark.entity.lab;

import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "LabResearchResultsEmployeeNotification")
public class LabResearchResultsEmployeeNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private LabResearchNotificationType type;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "destination", nullable = false)
    private String destination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public LabResearchNotificationType getType() {
        return type;
    }

    public void setType(LabResearchNotificationType type) {
        this.type = type;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
