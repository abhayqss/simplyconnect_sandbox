package com.scnsoft.eldermark.entity.document.facesheet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class Language extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn(name = "ability_mode_id")
    private CcdCode abilityMode;

    @ManyToOne
    @JoinColumn(name = "ability_proficiency_id")
    private CcdCode abilityProficiency;

    @Column(name = "preference_ind")
    private Boolean preferenceInd;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public CcdCode getAbilityMode() {
        return abilityMode;
    }

    public void setAbilityMode(CcdCode abilityMode) {
        this.abilityMode = abilityMode;
    }

    public CcdCode getAbilityProficiency() {
        return abilityProficiency;
    }

    public void setAbilityProficiency(CcdCode abilityProficiency) {
        this.abilityProficiency = abilityProficiency;
    }

    public Boolean getPreferenceInd() {
        return preferenceInd;
    }

    public void setPreferenceInd(Boolean preferenceInd) {
        this.preferenceInd = preferenceInd;
    }
}
