package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The pressure ulcer observation contains details about the pressure ulcer such as the stage of the ulcer, location, and dimensions.
 * If the pressure ulcer is a diagnosis, you may find this on the problem list.
 *
 * Deprecated in C-CDA R2 (2015). Use Longitudinal Care Wound Observation (2.16.840.1.113883.10.20.22.4.114) instead.
 */
@Entity
public class PressureUlcerObservation extends BasicEntity {

    /**
     * {@code negationInd = true} indicates that the problem was not observed
     */
    @Column(name = "negation_ind")
    private Boolean negationInd;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @Lob
    private String text;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pressureUlcerObservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TargetSiteCode> targetSiteCodes;

    @Column(name = "length_of_wound_value")
    private Double lengthOfWoundValue;

    @Column(name = "width_of_wound_value")
    private Double widthOfWoundValue;

    @Column(name = "depth_of_wound_value")
    private Double depthOfWoundValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_status_id", nullable = false)
    private FunctionalStatus functionalStatus;

    public Boolean isNegationInd() {
        return negationInd;
    }

    public void setNegationInd(Boolean negationInd) {
        this.negationInd = negationInd;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public List<TargetSiteCode> getTargetSiteCodes() {
        return targetSiteCodes;
    }

    public void setTargetSiteCodes(List<TargetSiteCode> targetSiteCodes) {
        this.targetSiteCodes = targetSiteCodes;
    }

    public Double getLengthOfWoundValue() {
        return lengthOfWoundValue;
    }

    public void setLengthOfWoundValue(Double lengthOfWoundValue) {
        this.lengthOfWoundValue = lengthOfWoundValue;
    }

    public Double getWidthOfWoundValue() {
        return widthOfWoundValue;
    }

    public void setWidthOfWoundValue(Double widthOfWoundValue) {
        this.widthOfWoundValue = widthOfWoundValue;
    }

    public Double getDepthOfWoundValue() {
        return depthOfWoundValue;
    }

    public void setDepthOfWoundValue(Double depthOfWoundValue) {
        this.depthOfWoundValue = depthOfWoundValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FunctionalStatus getFunctionalStatus() {
        return functionalStatus;
    }

    public void setFunctionalStatus(FunctionalStatus functionalStatus) {
        this.functionalStatus = functionalStatus;
    }
}
