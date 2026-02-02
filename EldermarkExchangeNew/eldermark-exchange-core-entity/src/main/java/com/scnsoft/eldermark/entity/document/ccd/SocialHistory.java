package com.scnsoft.eldermark.entity.document.ccd;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PregnancyObservation;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

@Entity
public class SocialHistory extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

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
    private Client client;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
