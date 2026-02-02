
package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class HighestPressureUlcerStage extends BasicEntity {
    private static final long serialVersionUID = 1L;

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
