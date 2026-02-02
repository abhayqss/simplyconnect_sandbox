package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class TargetSiteCode extends BasicEntity {
    @ManyToOne
    @JoinColumn
    private CcdCode code;

//    @Column(length=30)
//    private String name; //TODO do we need this field?

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

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

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
