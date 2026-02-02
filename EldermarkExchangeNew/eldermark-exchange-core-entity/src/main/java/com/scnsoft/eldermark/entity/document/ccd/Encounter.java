package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Encounter")
public class Encounter extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "encounter_type_code_id")
    private CcdCode encounterType;

    @Column(name = "encounter_type_text")
    private String encounterTypeText;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @ManyToOne
    @JoinColumn(name = "disposition_code_id")
    private CcdCode dispositionCode;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "encounter")
    private List<EncounterPerformer> encounterPerformers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Encounter_DeliveryLocation",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<ServiceDeliveryLocation> serviceDeliveryLocations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Encounter_Indication",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "indication_id"))
    private List<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "problem_observation_id")
    private ProblemObservation problemObservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(name = "ProcedureActivityEncounter",
            joinColumns = @JoinColumn(name = "encounter_id"),
            inverseJoinColumns = @JoinColumn(name = "procedure_activity_id"))
    private List<ProcedureActivity> procedureActivities;

    @Column(name = "consana_id")
    private String consanaId;

    public CcdCode getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(CcdCode encounterType) {
        this.encounterType = encounterType;
    }

    public String getEncounterTypeText() {
        return encounterTypeText;
    }

    public void setEncounterTypeText(String encounterTypeText) {
        this.encounterTypeText = encounterTypeText;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public CcdCode getDispositionCode() {
        return dispositionCode;
    }

    public void setDispositionCode(CcdCode dispositionCode) {
        this.dispositionCode = dispositionCode;
    }

    public List<EncounterPerformer> getEncounterPerformers() {
        return encounterPerformers;
    }

    public void setEncounterPerformers(List<EncounterPerformer> encounterPerformers) {
        this.encounterPerformers = encounterPerformers;
    }

    public List<ServiceDeliveryLocation> getServiceDeliveryLocations() {
        return serviceDeliveryLocations;
    }

    public void setServiceDeliveryLocations(List<ServiceDeliveryLocation> serviceDeliveryLocations) {
        this.serviceDeliveryLocations = serviceDeliveryLocations;
    }

    public List<Indication> getIndications() {
        return indications;
    }

    public void setIndications(List<Indication> indications) {
        this.indications = indications;
    }

    /**
     * @return ProblemObservation - a relevant problem or diagnose at the close of a visit or that needs to be
     * followed after the visit.
     */
    public ProblemObservation getProblemObservation() {
        return problemObservation;
    }

    /**
     * @param problemObservation a relevant problem or diagnose at the close of a visit or that needs to be
     *                           followed after the visit.
     */
    public void setProblemObservation(ProblemObservation problemObservation) {
        this.problemObservation = problemObservation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<ProcedureActivity> getProcedureActivities() {
        return procedureActivities;
    }

    public void setProcedureActivities(final List<ProcedureActivity> procedureActivities) {
        this.procedureActivities = procedureActivities;
    }

    public String getConsanaId() {
        return consanaId;
    }

    public void setConsanaId(String consanaId) {
        this.consanaId = consanaId;
    }
}