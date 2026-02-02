package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class HighestPressureUlcerStage extends BasicEntity {
    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_status_id", nullable = false)
    private FunctionalStatus functionalStatus;

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public FunctionalStatus getFunctionalStatus() {
        return functionalStatus;
    }

    public void setFunctionalStatus(FunctionalStatus functionalStatus) {
        this.functionalStatus = functionalStatus;
    }
}
