package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class FamilyHistoryObservation extends BasicEntity {


    @ManyToOne
    @JoinColumn(name = "problem_type_code_id")
    private CcdCode problemTypeCode;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @ManyToOne
    @JoinColumn(name = "problem_value_id")
    private CcdCode problemValue;

    @Column(name = "free_text_problem_value")
    private String freeTextProblemValue;

    @Column(name = "age_observation_value")
    private Integer ageObservationValue;

    @Column(length = 5, name = "age_observation_unit")
    private String ageObservationUnit;

    @Column(name = "deceased")
    private Boolean deceased;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_history_id", nullable = false)
    private FamilyHistory familyHistory;

    public CcdCode getProblemTypeCode() {
        return problemTypeCode;
    }

    public void setProblemTypeCode(CcdCode problemTypeCode) {
        this.problemTypeCode = problemTypeCode;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public CcdCode getProblemValue() {
        return problemValue;
    }

    public void setProblemValue(CcdCode problemValue) {
        this.problemValue = problemValue;
    }

    public String getFreeTextProblemValue() {
        return freeTextProblemValue;
    }

    public void setFreeTextProblemValue(String freeTextProblemValue) {
        this.freeTextProblemValue = freeTextProblemValue;
    }

    public Integer getAgeObservationValue() {
        return ageObservationValue;
    }

    public void setAgeObservationValue(Integer ageObservationValue) {
        this.ageObservationValue = ageObservationValue;
    }

    public String getAgeObservationUnit() {
        return ageObservationUnit;
    }

    public void setAgeObservationUnit(String ageObservationUnit) {
        this.ageObservationUnit = ageObservationUnit;
    }

    public Boolean getDeceased() {
        return deceased;
    }

    public void setDeceased(Boolean deceased) {
        this.deceased = deceased;
    }

    public FamilyHistory getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(FamilyHistory familyHistory) {
        this.familyHistory = familyHistory;
    }


}
