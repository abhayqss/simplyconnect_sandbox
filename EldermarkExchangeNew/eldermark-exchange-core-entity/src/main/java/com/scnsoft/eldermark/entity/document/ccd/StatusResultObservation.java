package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.scnsoft.eldermark.entity.assessment.AssessmentScaleObservation;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class StatusResultObservation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @Lob
    private String text;

    @ManyToOne
    @JoinColumn(name = "value_code_id")
    private CcdCode valueCode;

    @Column(length = 30)
    private String value;

    @Column(name = "value_unit", length = 10)
    private String valueUnit;

    @ManyToMany
    @JoinTable(name = "StatusResultObservation_InterpretationCode",
            joinColumns = @JoinColumn( name="result_observation_id"),
            inverseJoinColumns = @JoinColumn( name="interpretation_code_id"))
    private List<CcdCode> interpretationCodes;

    @ManyToOne
    @JoinColumn(name = "method_code_id")
    private CcdCode methodCode;

    @ManyToOne
    @JoinColumn(name = "target_site_code_id")
    private CcdCode targetSiteCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "author_id")
    private Author author;

    @ElementCollection
    @CollectionTable(name = "StatusResultObservationRange",
            joinColumns = @JoinColumn(name = "result_observation_id"))
    @Column(name = "result_range")
    private List<String> referenceRanges;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusResultObservation_CaregiverCharacteristic",
            joinColumns = @JoinColumn(name = "status_result_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "caregiver_characteristic_id"))
    private List<CaregiverCharacteristic> caregiverCharacteristics;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusResultObservation_AssessmentScaleObservation",
            joinColumns = @JoinColumn(name = "status_result_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "assessment_scale_observation_id"))
    private List<AssessmentScaleObservation> assessmentScaleObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusResultObservation_NonMedicinalSupplyActivity",
            joinColumns = @JoinColumn(name = "status_result_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "non_medicinal_supply_activity_id"))
    private List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CcdCode> getInterpretationCodes() {
        return interpretationCodes;
    }

    public void setInterpretationCodes(List<CcdCode> interpretationCodes) {
        this.interpretationCodes = interpretationCodes;
    }

    public CcdCode getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(CcdCode methodCode) {
        this.methodCode = methodCode;
    }

    public CcdCode getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(CcdCode targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<String> getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(List<String> referenceRanges) {
        this.referenceRanges = referenceRanges;
    }

    public List<CaregiverCharacteristic> getCaregiverCharacteristics() {
        return caregiverCharacteristics;
    }

    public void setCaregiverCharacteristics(List<CaregiverCharacteristic> caregiverCharacteristics) {
        this.caregiverCharacteristics = caregiverCharacteristics;
    }

    public List<AssessmentScaleObservation> getAssessmentScaleObservations() {
        return assessmentScaleObservations;
    }

    public void setAssessmentScaleObservations(List<AssessmentScaleObservation> assessmentScaleObservations) {
        this.assessmentScaleObservations = assessmentScaleObservations;
    }

    public List<NonMedicinalSupplyActivity> getNonMedicinalSupplyActivities() {
        return nonMedicinalSupplyActivities;
    }

    public void setNonMedicinalSupplyActivities(List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities) {
        this.nonMedicinalSupplyActivities = nonMedicinalSupplyActivities;
    }

    public String getValueUnit() {
        return valueUnit;
    }

    public void setValueUnit(String valueUnit) {
        this.valueUnit = valueUnit;
    }

    public CcdCode getValueCode() {
        return valueCode;
    }

    public void setValueCode(CcdCode valueCode) {
        this.valueCode = valueCode;
    }
}
