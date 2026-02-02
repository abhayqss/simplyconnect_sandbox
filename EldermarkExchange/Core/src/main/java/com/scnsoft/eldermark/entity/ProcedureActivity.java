package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class ProcedureActivity extends LegacyIdAwareEntity {
    @ManyToOne
    @JoinColumn(name = "procedure_type_code_id")
    private CcdCode procedureType;

    @Lob
    @Column(name = "procedure_type_text")
    private String procedureTypeText;

    @Column(length = 50, name = "mood_code")
    private String moodCode;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "value_text")
    private String valueText;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @Column(name = "procedure_started")
    private Date procedureStarted;

    @Column(name = "procedure_stopped")
    private Date procedureStopped;

    @ManyToOne
    @JoinColumn(name="priority_code_id")
    private CcdCode priorityCode;

    @ManyToOne
    @JoinColumn(name="method_code_id")
    private CcdCode methodCode;

    @ManyToMany
    @JoinTable(name = "ProcedureActivity_BodySiteCode",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="body_site_code_id"))
    private Set<CcdCode> bodySiteCodes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "ProcedureActivity_Performer",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="organization_id") )
    private Set<Organization> performers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ProcedureActivity_ProductInstance",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="product_id") )
    private Set<ProductInstance> productInstances;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ProcedureActivity_DeliveryLocation",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="location_id") )
    private Set<ServiceDeliveryLocation> serviceDeliveryLocations;

    @ElementCollection
    @CollectionTable(name="ProcedureActivityEncounter",
            joinColumns=@JoinColumn(name="procedure_activity_id"))
    @Column(name="encounter_id")
    private Set<String> encounterIds;

    @ElementCollection
    @CollectionTable(name="ProcedureActivitySpecimen",
            joinColumns=@JoinColumn(name="procedure_activity_id"))
    @Column(name="specimen_id")
    private Set<String> specimenIds;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="instructions_id")
    private Instructions instructions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ProcedureActivity_Indication",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="indication_id") )
    private Set<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_id")
    private Medication medication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "Procedure_ActivityAct",
            joinColumns = @JoinColumn( name="procedure_act_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_id"))
    private Procedure procedureIfAct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "Procedure_ActivityObservation",
            joinColumns = @JoinColumn( name="procedure_observation_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_id"))
    private Procedure procedureIfObservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "Procedure_ActivityProcedure",
            joinColumns = @JoinColumn( name="procedure_activity_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_id"))
    private Procedure procedureIfProcedure;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public CcdCode getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(CcdCode procedureType) {
        this.procedureType = procedureType;
    }

    public Date getProcedureStarted() {
        return procedureStarted;
    }

    public void setProcedureStarted(Date procedureStarted) {
        this.procedureStarted = procedureStarted;
    }

    public Date getProcedureStopped() {
        return procedureStopped;
    }

    public void setProcedureStopped(Date procedureStopped) {
        this.procedureStopped = procedureStopped;
    }

    public CcdCode getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(CcdCode priorityCode) {
        this.priorityCode = priorityCode;
    }

    public CcdCode getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(CcdCode methodCode) {
        this.methodCode = methodCode;
    }

    public Set<CcdCode> getBodySiteCodes() {
        return bodySiteCodes;
    }

    public void setBodySiteCodes(Set<CcdCode> bodySiteCodes) {
        this.bodySiteCodes = bodySiteCodes;
    }

    public Set<Organization> getPerformers() {
        return performers;
    }

    public Set<ProductInstance> getProductInstances() {
        return productInstances;
    }

    public void setProductInstances(Set<ProductInstance> productInstances) {
        this.productInstances = productInstances;
    }

    public Set<ServiceDeliveryLocation> getServiceDeliveryLocations() {
        return serviceDeliveryLocations;
    }

    public void setServiceDeliveryLocations(Set<ServiceDeliveryLocation> serviceDeliveryLocations) {
        this.serviceDeliveryLocations = serviceDeliveryLocations;
    }

    public Set<String> getEncounterIds() {
        return encounterIds;
    }

    public void setEncounterIds(Set<String> encounterIds) {
        this.encounterIds = encounterIds;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }


    public void setPerformers(Set<Organization> performers) {
        this.performers = performers;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public Set<Indication> getIndications() {
        return indications;
    }

    public void setIndications(Set<Indication> indications) {
        this.indications = indications;
    }

    public Set<String> getSpecimenIds() {
        return specimenIds;
    }

    public void setSpecimenIds(Set<String> specimenIds) {
        this.specimenIds = specimenIds;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public String getProcedureTypeText() {
        return procedureTypeText;
    }

    public void setProcedureTypeText(String procedureTypeText) {
        this.procedureTypeText = procedureTypeText;
    }

    public Procedure getProcedureIfAct() {
        return procedureIfAct;
    }

    public void setProcedureIfAct(Procedure procedureIfAct) {
        this.procedureIfAct = procedureIfAct;
    }

    public Procedure getProcedureIfObservation() {
        return procedureIfObservation;
    }

    public void setProcedureIfObservation(Procedure procedureIfObservation) {
        this.procedureIfObservation = procedureIfObservation;
    }

    public Procedure getProcedureIfProcedure() {
        return procedureIfProcedure;
    }

    public void setProcedureIfProcedure(Procedure procedureIfProcedure) {
        this.procedureIfProcedure = procedureIfProcedure;
    }

    public Procedure getProcedure() {
        if (getProcedureIfProcedure() != null) {
            return getProcedureIfProcedure();
        }
        if (getProcedureIfAct() != null) {
            return getProcedureIfAct();
        }
        return getProcedureIfObservation();
    }
}
