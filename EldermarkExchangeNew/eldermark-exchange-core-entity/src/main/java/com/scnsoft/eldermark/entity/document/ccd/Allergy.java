package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Allergy")
public class Allergy extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    /**
     * This is the time stamp for when the allergy was first documented as a concern.
     * (if the statusCode = "active")
     */
    @Column(name = "effective_time_low")
    private Date timeLow;
    @Column(name = "effective_time_high")
    private Date timeHigh;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "allergy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AllergyObservation> allergyObservations;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "al1_id")
    private Long al1Id;

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

    public Long getAl1Id() {
        return al1Id;
    }

    public void setAl1Id(final Long al1Id) {
        this.al1Id = al1Id;
    }
}
