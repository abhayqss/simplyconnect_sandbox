package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Allergy")
public class Allergy extends LegacyIdAwareEntity {
    @Column(length = 50, name = "status_code")
    private String statusCode;

    /**
     * This is the time stamp for when the allergy was first documented as a concern.
     * (if the statusCode = "active")
     */
    @Column(name = "effective_time_low")
    private Date timeLow;

    /**
     *
     * (if the statusCode="completed")
     */
    @Column(name = "effective_time_high")
    private Date timeHigh;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "allergy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AllergyObservation> allergyObservations;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long residentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public Set<AllergyObservation> getAllergyObservations() {
        return allergyObservations;
    }

    public void setAllergyObservations(Set<AllergyObservation> allergyObservations) {
        this.allergyObservations = allergyObservations;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
