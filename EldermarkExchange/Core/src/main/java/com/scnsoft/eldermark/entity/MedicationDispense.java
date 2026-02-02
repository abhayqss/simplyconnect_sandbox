package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MedicationDispense extends LegacyTableAwareEntity {
    @Column(length = 50, name = "prescription_number")
    private String prescriptionNumber;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "effective_time_low")
    private Date dispenseDateLow;

    @Column(name = "effective_time_high")
    private Date dispenseDateHigh;

    @Column(name = "repeat_number")
    private Integer fillNumber;

    @Column
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_information_id")
    private MedicationInformation medicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="immunization_medication_information_id")
    private ImmunizationMedicationInformation immunizationMedicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Organization provider;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getDispenseDateLow() {
        return dispenseDateLow;
    }

    public void setDispenseDateLow(Date dispenseDateLow) {
        this.dispenseDateLow = dispenseDateLow;
    }

    public Date getDispenseDateHigh() {
        return dispenseDateHigh;
    }

    public void setDispenseDateHigh(Date dispenseDateHigh) {
        this.dispenseDateHigh = dispenseDateHigh;
    }

    public Integer getFillNumber() {
        return fillNumber;
    }

    public void setFillNumber(Integer fillNumber) {
        this.fillNumber = fillNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public MedicationInformation getMedicationInformation() {
        return medicationInformation;
    }

    public void setMedicationInformation(MedicationInformation medicationInformation) {
        this.medicationInformation = medicationInformation;
    }

    public ImmunizationMedicationInformation getImmunizationMedicationInformation() {
        return immunizationMedicationInformation;
    }

    public void setImmunizationMedicationInformation(ImmunizationMedicationInformation immunizationMedicationInformation) {
        this.immunizationMedicationInformation = immunizationMedicationInformation;
    }

    public MedicationSupplyOrder getMedicationSupplyOrder() {
        return medicationSupplyOrder;
    }

    public void setMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder) {
        this.medicationSupplyOrder = medicationSupplyOrder;
    }

    public Organization getProvider() {
        return provider;
    }

    public void setProvider(Organization provider) {
        this.provider = provider;
    }
}
