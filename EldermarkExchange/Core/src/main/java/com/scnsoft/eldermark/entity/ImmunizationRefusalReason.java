package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "ImmunizationRefusalReason")
public class ImmunizationRefusalReason extends BasicEntity {
    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }
}
