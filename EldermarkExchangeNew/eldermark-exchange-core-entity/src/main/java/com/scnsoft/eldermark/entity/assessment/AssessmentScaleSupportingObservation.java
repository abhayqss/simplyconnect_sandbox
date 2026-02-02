package com.scnsoft.eldermark.entity.assessment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class AssessmentScaleSupportingObservation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn(name = "value_code_id")
    private CcdCode valueCode;

    @Column(name = "int_value")
    private Integer intValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_scale_observation_id", nullable = false)
    private AssessmentScaleObservation assessmentScaleObservation;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public void setValueCode(CcdCode valueCode) {
        this.valueCode = valueCode;
    }

    public CcdCode getValueCode() {
        return valueCode;
    }

    public AssessmentScaleObservation getAssessmentScaleObservation() {
        return assessmentScaleObservation;
    }

    public void setAssessmentScaleObservation(AssessmentScaleObservation assessmentScaleObservation) {
        this.assessmentScaleObservation = assessmentScaleObservation;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
}
