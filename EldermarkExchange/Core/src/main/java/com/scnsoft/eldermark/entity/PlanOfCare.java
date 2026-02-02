package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQuery(name = "planOfCare.findFreeText", query = "select p.freeText from PlanOfCare p where p.id = :id")
public class PlanOfCare extends ExchangeDocumentAwareBasicEntity {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_Act",
        joinColumns = @JoinColumn(name = "plan_of_care_id"),
        inverseJoinColumns = @JoinColumn(name = "act_id"))
    private List<PlanOfCareActivity> planOfCareActivityActList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_Encounter",
            joinColumns = @JoinColumn(name = "plan_of_care_id"),
            inverseJoinColumns = @JoinColumn(name = "encounter_id"))
    private List<PlanOfCareActivity> planOfCareActivityEncounterList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_Observation",
            joinColumns = @JoinColumn(name = "plan_of_care_id"),
            inverseJoinColumns = @JoinColumn(name = "observation_id"))
    private List<PlanOfCareActivity> planOfCareActivityObservationList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_Procedure",
            joinColumns = @JoinColumn(name = "plan_of_care_id"),
            inverseJoinColumns = @JoinColumn(name = "procedure_id"))
    private List<PlanOfCareActivity> planOfCareActivityProcedureList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_SubstanceAdministration",
            joinColumns = @JoinColumn(name = "plan_of_care_id"),
            inverseJoinColumns = @JoinColumn(name = "substance_administration_id"))
    private List<PlanOfCareActivity> planOfCareActivitySubstanceAdministrationList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="PlanOfCare_Supply",
            joinColumns = @JoinColumn(name = "plan_of_care_id"),
            inverseJoinColumns = @JoinColumn(name = "supply_id"))
    private List<PlanOfCareActivity> planOfCareActivitySupplyList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "PlanOfCare_Instructions",
            joinColumns = @JoinColumn( name="plan_of_care_id"),
            inverseJoinColumns = @JoinColumn( name="instruction_id") )
    private List<Instructions> instructions;

    @OneToOne(fetch = FetchType.LAZY)
    private Resident resident;

    @Lob
    @Column(name = "free_text")
    private String freeText;

    public List<PlanOfCareActivity> getPlanOfCareActivityActList() {
        return planOfCareActivityActList;
    }

    public void setPlanOfCareActivityActList(List<PlanOfCareActivity> planOfCareActivityActList) {
        this.planOfCareActivityActList = planOfCareActivityActList;
    }

    public List<PlanOfCareActivity> getPlanOfCareActivityEncounterList() {
        return planOfCareActivityEncounterList;
    }

    public void setPlanOfCareActivityEncounterList(List<PlanOfCareActivity> planOfCareActivityEncounterList) {
        this.planOfCareActivityEncounterList = planOfCareActivityEncounterList;
    }

    public List<PlanOfCareActivity> getPlanOfCareActivityObservationList() {
        return planOfCareActivityObservationList;
    }

    public void setPlanOfCareActivityObservationList(List<PlanOfCareActivity> planOfCareActivityObservationList) {
        this.planOfCareActivityObservationList = planOfCareActivityObservationList;
    }

    public List<PlanOfCareActivity> getPlanOfCareActivityProcedureList() {
        return planOfCareActivityProcedureList;
    }

    public void setPlanOfCareActivityProcedureList(List<PlanOfCareActivity> planOfCareActivityProcedureList) {
        this.planOfCareActivityProcedureList = planOfCareActivityProcedureList;
    }

    public List<PlanOfCareActivity> getPlanOfCareActivitySubstanceAdministrationList() {
        return planOfCareActivitySubstanceAdministrationList;
    }

    public void setPlanOfCareActivitySubstanceAdministrationList(List<PlanOfCareActivity> planOfCareActivitySubstanceAdministrationList) {
        this.planOfCareActivitySubstanceAdministrationList = planOfCareActivitySubstanceAdministrationList;
    }

    public List<PlanOfCareActivity> getPlanOfCareActivitySupplyList() {
        return planOfCareActivitySupplyList;
    }

    public void setPlanOfCareActivitySupplyList(List<PlanOfCareActivity> planOfCareActivitySupplyList) {
        this.planOfCareActivitySupplyList = planOfCareActivitySupplyList;
    }

    public List<Instructions> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instructions> instructions) {
        this.instructions = instructions;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

}
