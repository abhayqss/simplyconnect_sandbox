package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.ImmunizationMedicationInformation;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MedicationSupplyOrder extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @Column
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medication_information_id")
    private MedicationInformation medicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "immunization_medication_information_id")
    private ImmunizationMedicationInformation immunizationMedicationInformation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "instructions_id")
    private Instructions instructions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "medical_professional_id")
    private MedicalProfessional medicalProfessional;

    @Column(name = "daw_product_selection_code")
    private String DAWProductSelectionCode;

    @Column(name = "prescription_origin_code")
    private String prescriptionOriginCode;

    @Column(name = "prescription_number")
    private String prescriptionNumber;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Integer getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(Integer repeatNumber) {
        this.repeatNumber = repeatNumber;
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public MedicalProfessional getMedicalProfessional() {
        return medicalProfessional;
    }

    public void setMedicalProfessional(MedicalProfessional medicalProfessional) {
        this.medicalProfessional = medicalProfessional;
    }

    public String getDAWProductSelectionCode() {
        return DAWProductSelectionCode;
    }

    public void setDAWProductSelectionCode(String DAWProductSelectionCode) {
        this.DAWProductSelectionCode = DAWProductSelectionCode;
    }

    public String getPrescriptionOriginCode() {
        return prescriptionOriginCode;
    }

    public void setPrescriptionOriginCode(String prescriptionOriginCode) {
        this.prescriptionOriginCode = prescriptionOriginCode;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }
}
