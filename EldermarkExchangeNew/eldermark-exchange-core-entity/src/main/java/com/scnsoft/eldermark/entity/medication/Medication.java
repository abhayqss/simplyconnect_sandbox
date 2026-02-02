package com.scnsoft.eldermark.entity.medication;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.document.ccd.MedicationPrecondition;
import com.scnsoft.eldermark.entity.document.ccd.ReactionObservation;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Medication", indexes = {@Index(name = "IX_medication_resident", columnList = "resident_id")})
public class Medication extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

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
    @JoinColumn(name = "medication_information_id")
    private MedicationInformation medicationInformation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_DrugVehicle", joinColumns = @JoinColumn(name = "medication_id"), inverseJoinColumns = @JoinColumn(name = "drug_vehicle_id"))
    private List<DrugVehicle> drugVehicles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_Indication", joinColumns = @JoinColumn(name = "medication_id"), inverseJoinColumns = @JoinColumn(name = "indication_id"))
    private List<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "instructions_id")
    private Instructions instructions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "medication")
    private List<MedicationDispense> medicationDispenses;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reaction_observation_id")
    private ReactionObservation reactionObservation;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_MedicationPrecondition", joinColumns = @JoinColumn(name = "medication_id"), inverseJoinColumns = @JoinColumn(name = "precondition_id"))
    private List<MedicationPrecondition> preconditions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "end_date_future")
    private Instant endDateFuture;

    @Column(name = "pharmacy_origin_date")
    private Instant pharmacyOriginDate;

    @Column(name = "refill_date")
    private Instant refillDate;

    @Column(name = "pharm_rx_id")
    private String pharmRxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Community pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_pharmacy_id")
    private Community dispensingPharmacy;

    @Column(name = "prn_scheduled")
    private Boolean prnScheduled;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "recurrence")
    private String recurrence;

    @Column(name = "consana_id")
    private String consanaId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "medi_span_id")
    private String mediSpanId;

    @Column(name = "creation_datetime")
    private Instant creationDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @Column(name = "update_datetime")
    private Instant updateDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Employee updatedBy;

    @Column(name = "is_manually_created")
    private Boolean isManuallyCreated;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "medication")
    private MedicationReport medicationReport;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public Instant getEndDateFuture() {
        return endDateFuture;
    }

    public void setEndDateFuture(Instant endDateFuture) {
        this.endDateFuture = endDateFuture;
    }

    public Instant getPharmacyOriginDate() {
        return pharmacyOriginDate;
    }

    public void setPharmacyOriginDate(Instant pharmacyOriginDate) {
        this.pharmacyOriginDate = pharmacyOriginDate;
    }

    public Instant getRefillDate() {
        return refillDate;
    }

    public void setRefillDate(Instant refillDate) {
        this.refillDate = refillDate;
    }

    public String getPharmRxId() {
        return pharmRxId;
    }

    public void setPharmRxId(String pharmRxId) {
        this.pharmRxId = pharmRxId;
    }

    public Community getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Community pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Community getDispensingPharmacy() {
        return dispensingPharmacy;
    }

    public void setDispensingPharmacy(Community dispensingPharmacy) {
        this.dispensingPharmacy = dispensingPharmacy;
    }

    public Boolean getPrnScheduled() {
        return prnScheduled;
    }

    public void setPrnScheduled(Boolean prnScheduled) {
        this.prnScheduled = prnScheduled;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getConsanaId() {
        return consanaId;
    }

    public void setConsanaId(String consanaId) {
        this.consanaId = consanaId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMediSpanId() {
        return mediSpanId;
    }

    public void setMediSpanId(String mediSpanId) {
        this.mediSpanId = mediSpanId;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Instant updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public Employee getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Employee updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getManuallyCreated() {
        return isManuallyCreated;
    }

    public void setManuallyCreated(Boolean manuallyCreated) {
        isManuallyCreated = manuallyCreated;
    }

    public MedicationReport getMedicationReport() {
        return medicationReport;
    }

    public void setMedicationReport(MedicationReport medicationReport) {
        this.medicationReport = medicationReport;
    }
}
