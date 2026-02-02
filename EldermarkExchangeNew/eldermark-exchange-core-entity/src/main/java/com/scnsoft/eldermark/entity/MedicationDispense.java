package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.ImmunizationMedicationInformation;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class MedicationDispense extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

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

    @Column(name = "quantity", columnDefinition = "decimal")
    private BigDecimal quantity;

    //todo add to CCD
    @Column(name = "quantity_qualifier_code")
    private String quantityQualifierCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medication_information_id")
    private MedicationInformation medicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "immunization_medication_information_id")
    private ImmunizationMedicationInformation immunizationMedicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organization_id")
    private Community provider;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medication_supply_order_id")
    private MedicationSupplyOrder medicationSupplyOrder;

    @ManyToOne
    @JoinTable(name = "Medication_MedicationDispense",
            joinColumns = @JoinColumn(name = "medication_dispense_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private Medication medication;

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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getQuantityQualifierCode() {
        return quantityQualifierCode;
    }

    public void setQuantityQualifierCode(String quantityQualifierCode) {
        this.quantityQualifierCode = quantityQualifierCode;
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

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public Community getProvider() {
        return provider;
    }

    public void setProvider(Community provider) {
        this.provider = provider;
    }
}
