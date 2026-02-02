package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.MedicationSupplyOrder;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "Immunization")
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 32))
public class Immunization extends StringLegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(length = 50, name = "mood_code")
    private String moodCode;

    @Column(name = "refusal")
    private Boolean refusal;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Lob
    @Column(name = "text")
    private String text;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "immunization_started")
    private Date immunizationStarted;

    @Column(name = "immunization_stopped")
    private Date immunizationStopped;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @Column(name = "repeat_number_mood", length = 50)
    private String repeatNumberMood;

    @ManyToOne
    @JoinColumn(name = "route_code_id")
    private CcdCode route;

    @ManyToOne
    @JoinColumn(name = "site_code_id")
    private CcdCode site;

    @Column(name = "dose_quantity")
    private Integer doseQuantity;

    @Column(length = 50, name = "dose_units")
    private String doseUnits;

    @ManyToOne
    @JoinColumn(name = "administration_unit_code_id")
    private CcdCode administrationUnitCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="immunization_medication_information_id")
    private ImmunizationMedicationInformation immunizationMedicationInformation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Immunization_DrugVehicle",
            joinColumns = @JoinColumn( name="immunization_id"),
            inverseJoinColumns = @JoinColumn( name="drug_vehicle_id") )
    private List<DrugVehicle> drugVehicles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Immunization_Indication",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="indication_id") )
    private List<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="instructions_id")
    private Instructions instructions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_dispense_id")
    private MedicationDispense medicationDispense;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="reaction_observation_id")
    private ReactionObservation reactionObservation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Immunization_MedicationPrecondition",
            joinColumns = @JoinColumn( name="immunization_id"),
            inverseJoinColumns = @JoinColumn( name="precondition_id") )
    private List<MedicationPrecondition> preconditions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="immunization_refusal_reason_id")
    private ImmunizationRefusalReason immunizationRefusalReason;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="person_id")
    private Person performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public Boolean getRefusal() {
        return refusal;
    }

    public void setRefusal(Boolean refusal) {
        this.refusal = refusal;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getImmunizationStarted() {
        return immunizationStarted;
    }

    public void setImmunizationStarted(Date immunizationStarted) {
        this.immunizationStarted = immunizationStarted;
    }

    public Date getImmunizationStopped() {
        return immunizationStopped;
    }

    public void setImmunizationStopped(Date immunizationStopped) {
        this.immunizationStopped = immunizationStopped;
    }

    public Integer getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(Integer repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public String getRepeatNumberMood() {
        return repeatNumberMood;
    }

    public void setRepeatNumberMood(String repeatNumberMood) {
        this.repeatNumberMood = repeatNumberMood;
    }

    public Integer getDoseQuantity() {
        return doseQuantity;
    }

    public void setDoseQuantity(Integer doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    public String getDoseUnits() {
        return doseUnits;
    }

    public void setDoseUnits(String doseUnits) {
        this.doseUnits = doseUnits;
    }

    public ImmunizationMedicationInformation getImmunizationMedicationInformation() {
        return immunizationMedicationInformation;
    }

    public void setImmunizationMedicationInformation(ImmunizationMedicationInformation immunizationMedicationInformation) {
        this.immunizationMedicationInformation = immunizationMedicationInformation;
    }

    public List<DrugVehicle> getDrugVehicles() {
        return drugVehicles;
    }

    public void setDrugVehicles(List<DrugVehicle> drugVehicles) {
        this.drugVehicles = drugVehicles;
    }

    public List<Indication> getIndications() {
        return indications;
    }

    public void setIndications(List<Indication> indications) {
        this.indications = indications;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public MedicationSupplyOrder getMedicationSupplyOrder() {
        return medicationSupplyOrder;
    }

    public void setMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder) {
        this.medicationSupplyOrder = medicationSupplyOrder;
    }

    public MedicationDispense getMedicationDispense() {
        return medicationDispense;
    }

    public void setMedicationDispense(MedicationDispense medicationDispense) {
        this.medicationDispense = medicationDispense;
    }

    public ReactionObservation getReactionObservation() {
        return reactionObservation;
    }

    public void setReactionObservation(ReactionObservation reactionObservation) {
        this.reactionObservation = reactionObservation;
    }

    public List<MedicationPrecondition> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<MedicationPrecondition> preconditions) {
        this.preconditions = preconditions;
    }

    public ImmunizationRefusalReason getImmunizationRefusalReason() {
        return immunizationRefusalReason;
    }

    public void setImmunizationRefusalReason(ImmunizationRefusalReason immunizationRefusalReason) {
        this.immunizationRefusalReason = immunizationRefusalReason;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Person getPerformer() {
        return performer;
    }

    public void setPerformer(Person performer) {
        this.performer = performer;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public CcdCode getRoute() {
        return route;
    }

    public void setRoute(CcdCode route) {
        this.route = route;
    }

    public CcdCode getSite() {
        return site;
    }

    public void setSite(CcdCode site) {
        this.site = site;
    }

    public CcdCode getAdministrationUnitCode() {
        return administrationUnitCode;
    }

    public void setAdministrationUnitCode(CcdCode administrationUnitCode) {
        this.administrationUnitCode = administrationUnitCode;
    }

}
