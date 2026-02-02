package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class PlanOfCareActivity extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "mood_code", length = 50, nullable = false)
    private String moodCode;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Act",
            joinColumns = @JoinColumn(name = "act_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfAct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Encounter",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfEncounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Instructions",
            joinColumns = @JoinColumn(name = "instruction_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfInstruction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Observation",
            joinColumns = @JoinColumn(name = "observation_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfObservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Procedure",
            joinColumns = @JoinColumn(name = "procedure_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfProcedure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_SubstanceAdministration",
            joinColumns = @JoinColumn(name = "substance_administration_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfSubstanceAdministration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PlanOfCare_Supply",
            joinColumns = @JoinColumn(name = "supply_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_of_care_id"))
    private PlanOfCare planOfCareIfSupply;


    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public PlanOfCare getPlanOfCareIfAct() {
        return planOfCareIfAct;
    }

    public void setPlanOfCareIfAct(PlanOfCare planOfCareIfAct) {
        this.planOfCareIfAct = planOfCareIfAct;
    }

    public PlanOfCare getPlanOfCareIfEncounter() {
        return planOfCareIfEncounter;
    }

    public void setPlanOfCareIfEncounter(PlanOfCare planOfCareIfEncounter) {
        this.planOfCareIfEncounter = planOfCareIfEncounter;
    }

    public PlanOfCare getPlanOfCareIfInstruction() {
        return planOfCareIfInstruction;
    }

    public void setPlanOfCareIfInstruction(PlanOfCare planOfCareIfInstruction) {
        this.planOfCareIfInstruction = planOfCareIfInstruction;
    }

    public PlanOfCare getPlanOfCareIfObservation() {
        return planOfCareIfObservation;
    }

    public void setPlanOfCareIfObservation(PlanOfCare planOfCareIfObservation) {
        this.planOfCareIfObservation = planOfCareIfObservation;
    }

    public PlanOfCare getPlanOfCareIfProcedure() {
        return planOfCareIfProcedure;
    }

    public void setPlanOfCareIfProcedure(PlanOfCare planOfCareIfProcedure) {
        this.planOfCareIfProcedure = planOfCareIfProcedure;
    }

    public PlanOfCare getPlanOfCareIfSubstanceAdministration() {
        return planOfCareIfSubstanceAdministration;
    }

    public void setPlanOfCareIfSubstanceAdministration(PlanOfCare planOfCareIfSubstanceAdministration) {
        this.planOfCareIfSubstanceAdministration = planOfCareIfSubstanceAdministration;
    }

    public PlanOfCare getPlanOfCareIfSupply() {
        return planOfCareIfSupply;
    }

    public void setPlanOfCareIfSupply(PlanOfCare planOfCareIfSupply) {
        this.planOfCareIfSupply = planOfCareIfSupply;
    }

    public PlanOfCare getPlanOfCare() {
        // only one of these fields should be not null
        if (getPlanOfCareIfAct() != null) {
            return getPlanOfCareIfAct();
        }
        if (getPlanOfCareIfEncounter() != null) {
            return getPlanOfCareIfEncounter();
        }
        if (getPlanOfCareIfInstruction() != null) {
            return getPlanOfCareIfInstruction();
        }
        if (getPlanOfCareIfObservation() != null) {
            return getPlanOfCareIfObservation();
        }
        if (getPlanOfCareIfProcedure() != null) {
            return getPlanOfCareIfProcedure();
        }
        if (getPlanOfCareIfSubstanceAdministration() != null) {
            return getPlanOfCareIfSubstanceAdministration();
        }
        return getPlanOfCareIfSupply();
    }

}
