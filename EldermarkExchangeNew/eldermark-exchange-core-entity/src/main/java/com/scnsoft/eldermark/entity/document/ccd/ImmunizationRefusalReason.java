package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "ImmunizationRefusalReason")
public class ImmunizationRefusalReason extends BasicEntity {
    private static final long serialVersionUID = 1L;

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
