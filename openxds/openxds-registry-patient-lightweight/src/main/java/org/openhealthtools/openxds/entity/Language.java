package org.openhealthtools.openxds.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Language extends LegacyIdAwareEntity {
    @ManyToOne
    @JoinColumn
    private Resident resident;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

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

    public Boolean getPreferenceInd() {
        return preferenceInd;
    }

    public void setPreferenceInd(Boolean preferenceInd) {
        this.preferenceInd = preferenceInd;
    }
}
