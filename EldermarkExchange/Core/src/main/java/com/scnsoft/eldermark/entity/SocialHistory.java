package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class SocialHistory extends LegacyIdAwareEntity {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "socialHistory")
    private List<SocialHistoryObservation> socialHistoryObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "socialHistory")
    private List<PregnancyObservation> pregnancyObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "socialHistory")
    private List<SmokingStatusObservation> smokingStatusObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "socialHistory")
    private List<TobaccoUse> tobaccoUses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    public List<SocialHistoryObservation> getSocialHistoryObservations() {
        return socialHistoryObservations;
    }

    public void setSocialHistoryObservations(List<SocialHistoryObservation> socialHistoryObservations) {
        this.socialHistoryObservations = socialHistoryObservations;
    }

    public List<PregnancyObservation> getPregnancyObservations() {
        return pregnancyObservations;
    }

    public void setPregnancyObservations(List<PregnancyObservation> pregnancyObservations) {
        this.pregnancyObservations = pregnancyObservations;
    }

    public List<SmokingStatusObservation> getSmokingStatusObservations() {
        return smokingStatusObservations;
    }

    public void setSmokingStatusObservations(List<SmokingStatusObservation> smokingStatusObservations) {
        this.smokingStatusObservations = smokingStatusObservations;
    }

    public List<TobaccoUse> getTobaccoUses() {
        return tobaccoUses;
    }

    public void setTobaccoUses(List<TobaccoUse> tobaccoUses) {
        this.tobaccoUses = tobaccoUses;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
