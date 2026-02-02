package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class TargetSiteCode extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pressure_ulcer_observation_id", nullable = false)
    private PressureUlcerObservation pressureUlcerObservation;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public PressureUlcerObservation getPressureUlcerObservation() {
        return pressureUlcerObservation;
    }

    public void setPressureUlcerObservation(PressureUlcerObservation pressureUlcerObservation) {
        this.pressureUlcerObservation = pressureUlcerObservation;
    }
}
