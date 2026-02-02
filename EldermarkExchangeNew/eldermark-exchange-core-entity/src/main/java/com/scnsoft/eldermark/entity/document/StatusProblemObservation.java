package com.scnsoft.eldermark.entity.document;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.scnsoft.eldermark.entity.assessment.AssessmentScaleObservation;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.ccd.CaregiverCharacteristic;
import com.scnsoft.eldermark.entity.document.ccd.NonMedicinalSupplyActivity;

@Entity
public class StatusProblemObservation extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Lob
    private String text;

    @Column(name = "negation_ind")
    private Boolean negationInd;

    @Column(name = "resolved")
    private Boolean resolved;

    @Column(name = "time_low")
    private Date timeLow;

    @Column(name = "time_high")
    private Date timeHigh;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @ManyToOne
    @JoinColumn(name = "method_code_id")
    private CcdCode methodCode;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusProblemObservation_NonMedicinalSupplyActivity",
            joinColumns = @JoinColumn(name = "status_problem_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "non_medicinal_supply_activity_id"))
    private List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusProblemObservation_CaregiverCharacteristic",
            joinColumns = @JoinColumn(name = "status_problem_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "caregiver_characteristic_id"))
    private List<CaregiverCharacteristic> caregiverCharacteristics;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "StatusProblemObservation_AssessmentScaleObservation",
            joinColumns = @JoinColumn(name = "status_problem_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "assessment_scale_observation_id"))
    private List<AssessmentScaleObservation> assessmentScaleObservations;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean isNegationInd() {
        return negationInd;
    }

    public void setNegationInd(Boolean negationInd) {
        this.negationInd = negationInd;
    }

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public CcdCode getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(CcdCode methodCode) {
        this.methodCode = methodCode;
    }

    public List<NonMedicinalSupplyActivity> getNonMedicinalSupplyActivities() {
        return nonMedicinalSupplyActivities;
    }

    public void setNonMedicinalSupplyActivities(List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities) {
        this.nonMedicinalSupplyActivities = nonMedicinalSupplyActivities;
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

    public Boolean isResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }
}
