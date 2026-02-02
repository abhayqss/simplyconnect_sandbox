package com.scnsoft.eldermark.entity.medication;

import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientMedicationSecurityAwareEntity;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.Indication;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ClientMedication")
public class ClientMedication implements ClientMedicationSecurityAwareEntity {
    //todo add more fields for details when needed
    @Id
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "free_text_sig")
    private String freeTextSig;

    @Column(name = "medication_started")
    private Instant medicationStarted;

    @Column(name = "medication_stopped")
    private Instant medicationStopped;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medication_information_id")
    private MedicationInformation medicationInformation;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ClientMedicationStatus status;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id")
    private Organization organization;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_Indication", joinColumns = @JoinColumn(name = "medication_id"), inverseJoinColumns = @JoinColumn(name = "indication_id"))
    private List<Indication> indications;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person performer;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "clientMedication")
    private MedicationReport medicationReport;

    @Column(name = "end_date_future")
    private Instant endDateFuture;

    @Column(name = "pharmacy_origin_date")
    private Instant pharmacyOriginDate;

    @Column(name = "refill_date")
    private Instant refillDate;

    @Column(name = "pharm_rx_id")
    private String pharmRxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_pharmacy_id")
    private Community dispensingPharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Community pharmacy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    @Column(name = "last_update ")
    private String lastUpdate;

    @Column(name = "stop_delivery_after_date")
    private Instant stopDeliveryAfterDate;

    @Column(name = "prn_scheduled")
    private Boolean prnScheduled;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "recurrence")
    private String recurrence;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFreeTextSig() {
        return freeTextSig;
    }

    public void setFreeTextSig(String freeTextSig) {
        this.freeTextSig = freeTextSig;
    }

    public Instant getMedicationStarted() {
        return medicationStarted;
    }

    public void setMedicationStarted(Instant medicationStarted) {
        this.medicationStarted = medicationStarted;
    }

    public Instant getMedicationStopped() {
        return medicationStopped;
    }

    public void setMedicationStopped(Instant medicationStopped) {
        this.medicationStopped = medicationStopped;
    }

    public MedicationInformation getMedicationInformation() {
        return medicationInformation;
    }

    public void setMedicationInformation(MedicationInformation medicationInformation) {
        this.medicationInformation = medicationInformation;
    }

    public ClientMedicationStatus getStatus() {
        return status;
    }

    public void setStatus(ClientMedicationStatus status) {
        this.status = status;
    }

    public Integer getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(Integer repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Indication> getIndications() {
        return indications;
    }

    public void setIndications(List<Indication> indications) {
        this.indications = indications;
    }

    public Person getPerformer() {
        return performer;
    }

    public void setPerformer(Person performer) {
        this.performer = performer;
    }

    public MedicationReport getMedicationReport() {
        return medicationReport;
    }

    public void setMedicationReport(MedicationReport medicationReport) {
        this.medicationReport = medicationReport;
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

    public Community getDispensingPharmacy() {
        return dispensingPharmacy;
    }

    public void setDispensingPharmacy(Community dispensingPharmacy) {
        this.dispensingPharmacy = dispensingPharmacy;
    }

    public Community getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Community pharmacy) {
        this.pharmacy = pharmacy;
    }

    public MedicationSupplyOrder getMedicationSupplyOrder() {
        return medicationSupplyOrder;
    }

    public void setMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder) {
        this.medicationSupplyOrder = medicationSupplyOrder;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Instant getStopDeliveryAfterDate() {
        return stopDeliveryAfterDate;
    }

    public void setStopDeliveryAfterDate(Instant stopDeliveryAfterDate) {
        this.stopDeliveryAfterDate = stopDeliveryAfterDate;
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

    @Override
    public Boolean getIsManuallyCreated() {
        return isManuallyCreated;
    }

    public void setIsManuallyCreated(Boolean manuallyCreated) {
        isManuallyCreated = manuallyCreated;
    }
}
