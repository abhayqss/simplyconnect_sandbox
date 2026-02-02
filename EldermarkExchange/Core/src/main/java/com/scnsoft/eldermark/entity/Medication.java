package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Medication", indexes = {
        @Index(name = "IX_medication_resident", columnList = "resident_id")
})
public class Medication extends LegacyIdAwareEntity {
    @Column(length = 50, name = "mood_code")
    private String moodCode;

    @ManyToOne
    @JoinColumn(name = "delivery_method_code_id")
    private CcdCode deliveryMethod;

    @Lob
    @Column(name = "free_text_sig")
    private String freeTextSig;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "medication_started")
    private Date medicationStarted;

    @Column(name = "medication_stopped")
    private Date medicationStopped;

    @Column(name = "administration_timing_period")
    private Integer administrationTimingPeriod;

    @Column(name = "administration_timing_unit")
    private String administrationTimingUnit;
    
    @Lob
    @Column(name = "administration_timing_value")
    private String administrationTimingValue;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @Column(length = 50, name = "repeat_number_mood")
    private String repeatNumberMood;

    @ManyToOne
    @JoinColumn(name = "route_code_id")
    private CcdCode route;

    @ManyToOne
    @JoinColumn(name = "site_code_id")
    private CcdCode site;

    @Column(name = "dose_quantity")
    private Integer doseQuantity;

    @Column(name = "max_dose_quantity")
    private Integer maxDoseQuantity;

    @Column(length = 50, name = "dose_units")
    private String doseUnits;

    @Column(name = "rate_quantity")
    private Integer rateQuantity;

    @Column(length = 50, name = "rate_units")
    private String rateUnits;

    @ManyToOne
    @JoinColumn(name = "administration_unit_code_id")
    private CcdCode administrationUnitCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_information_id")
    private MedicationInformation medicationInformation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_DrugVehicle",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="drug_vehicle_id") )
    private List<DrugVehicle> drugVehicles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_Indication",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="indication_id") )
    private List<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="instructions_id")
    private Instructions instructions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_MedicationDispense",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="medication_dispense_id") )
    private List<MedicationDispense> medicationDispenses;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="reaction_observation_id")
    private ReactionObservation reactionObservation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_MedicationPrecondition",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="precondition_id") )
    private List<MedicationPrecondition> preconditions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="person_id")
    private Person performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @OneToOne(mappedBy = "medication")
    private MedicationReport medicationReport;

    @Column(name = "consana_id")
    private String consanaId;

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public String getFreeTextSig() {
        return freeTextSig;
    }

    public void setFreeTextSig(String freeTextSig) {
        this.freeTextSig = freeTextSig;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getMedicationStopped() {
        return medicationStopped;
    }

    public void setMedicationStopped(Date medicationStopped) {
        this.medicationStopped = medicationStopped;
    }

    public Integer getAdministrationTimingPeriod() {
        return administrationTimingPeriod;
    }

    public void setAdministrationTimingPeriod(Integer administrationTimingPeriod) {
        this.administrationTimingPeriod = administrationTimingPeriod;
    }

    public String getAdministrationTimingUnit() {
        return administrationTimingUnit;
    }

    public void setAdministrationTimingUnit(String administrationTimingUnit) {
        this.administrationTimingUnit = administrationTimingUnit;
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

    public Integer getMaxDoseQuantity() {
        return maxDoseQuantity;
    }

    public void setMaxDoseQuantity(Integer maxDoseQuantity) {
        this.maxDoseQuantity = maxDoseQuantity;
    }

    public String getDoseUnits() {
        return doseUnits;
    }

    public void setDoseUnits(String doseUnits) {
        this.doseUnits = doseUnits;
    }

    public Integer getRateQuantity() {
        return rateQuantity;
    }

    public void setRateQuantity(Integer rateQuantity) {
        this.rateQuantity = rateQuantity;
    }

    public String getRateUnits() {
        return rateUnits;
    }

    public void setRateUnits(String rateUnits) {
        this.rateUnits = rateUnits;
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

    public CcdCode getRoute() {

        return route;
    }

    public void setRoute(CcdCode route) {
        this.route = route;
    }

    public MedicationInformation getMedicationInformation() {
        return medicationInformation;
    }

    public void setMedicationInformation(MedicationInformation medicationInformation) {
        this.medicationInformation = medicationInformation;
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

    public List<MedicationDispense> getMedicationDispenses() {
        return medicationDispenses;
    }

    public void setMedicationDispenses(List<MedicationDispense> medicationDispenses) {
        this.medicationDispenses = medicationDispenses;
    }

    public ReactionObservation getReactionObservation() {
        return reactionObservation;
    }

    public void setReactionObservation(ReactionObservation reactionObservation) {
        this.reactionObservation = reactionObservation;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Date getMedicationStarted() {
        return medicationStarted;
    }

    public void setMedicationStarted(Date medicationStarted) {
        this.medicationStarted = medicationStarted;
    }

    public List<MedicationPrecondition> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<MedicationPrecondition> preconditions) {
        this.preconditions = preconditions;
    }

    public Person getPerformer() {
        return performer;
    }

    public void setPerformer(Person performer) {
        this.performer = performer;
    }

    public CcdCode getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(CcdCode deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

	public String getAdministrationTimingValue() {
		return administrationTimingValue;
	}

	public void setAdministrationTimingValue(String administrationTimingValue) {
		this.administrationTimingValue = administrationTimingValue;
	}

    public MedicationReport getMedicationReport() {
        return medicationReport;
    }

    public void setMedicationReport(MedicationReport medicationReport) {
        this.medicationReport = medicationReport;
    }

    public String getConsanaId() {
        return consanaId;
    }

    public void setConsanaId(String consanaId) {
        this.consanaId = consanaId;
    }
}
