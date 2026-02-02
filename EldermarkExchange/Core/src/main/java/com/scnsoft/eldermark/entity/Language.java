package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class Language extends LegacyIdAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

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

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
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
