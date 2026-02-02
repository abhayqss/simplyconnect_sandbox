package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "AffiliatedRelationshipNotification")
public class AffiliatedRelationshipNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "primary_database_id", nullable = false)
    private Long primaryOrganizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_database_id", insertable = false, updatable = false)
    private Organization primaryOrganization;

    @Column(name = "affiliated_database_id", nullable = false)
    private Long affiliatedOrganizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliated_database_id", insertable = false, updatable = false)
    private Organization affiliatedOrganization;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Column(name = "sent_datetime", nullable = false)
    private Instant sentDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Employee receiver;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "is_terminated")
    private boolean isTerminated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrimaryOrganizationId() {
        return primaryOrganizationId;
    }

    public void setPrimaryOrganizationId(Long primaryOrganizationId) {
        this.primaryOrganizationId = primaryOrganizationId;
    }

    public Organization getPrimaryOrganization() {
        return primaryOrganization;
    }

    public void setPrimaryOrganization(Organization primaryOrganization) {
        this.primaryOrganization = primaryOrganization;
    }

    public Long getAffiliatedOrganizationId() {
        return affiliatedOrganizationId;
    }

    public void setAffiliatedOrganizationId(Long affiliatedOrganizationId) {
        this.affiliatedOrganizationId = affiliatedOrganizationId;
    }

    public Organization getAffiliatedOrganization() {
        return affiliatedOrganization;
    }

    public void setAffiliatedOrganization(Organization affiliatedOrganization) {
        this.affiliatedOrganization = affiliatedOrganization;
    }

    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Employee getReceiver() {
        return receiver;
    }

    public void setReceiver(Employee receiver) {
        this.receiver = receiver;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }
}
