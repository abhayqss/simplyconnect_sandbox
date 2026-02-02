package com.scnsoft.eldermark.dto.medication;

import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;

import java.time.Instant;

public class SaveMedicationRequest {

    private Long id;
    private Employee author;
    private String mediSpanId;
    private String ndcCode;
    private Client client;
    private PrescribedBy prescribedBy;
    private Integer prescriptionQuantity;
    private Instant prescribedDate;
    private Instant prescriptionExpirationDate;
    private String frequency;
    private String directions;
    private ClientMedicationStatus status;
    private Instant startedDate;
    private Instant stoppedDate;
    private String indicatedFor;
    private String comment;
    private String dosageQuantity;

    public static class PrescribedBy {

        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public String getMediSpanId() {
        return mediSpanId;
    }

    public void setMediSpanId(String mediSpanId) {
        this.mediSpanId = mediSpanId;
    }

    public String getNdcCode() {
        return ndcCode;
    }

    public void setNdcCode(String ndcCode) {
        this.ndcCode = ndcCode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PrescribedBy getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(PrescribedBy prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public Integer getPrescriptionQuantity() {
        return prescriptionQuantity;
    }

    public void setPrescriptionQuantity(Integer prescriptionQuantity) {
        this.prescriptionQuantity = prescriptionQuantity;
    }

    public Instant getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(Instant prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public Instant getPrescriptionExpirationDate() {
        return prescriptionExpirationDate;
    }

    public void setPrescriptionExpirationDate(Instant prescriptionExpirationDate) {
        this.prescriptionExpirationDate = prescriptionExpirationDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public ClientMedicationStatus getStatus() {
        return status;
    }

    public void setStatus(ClientMedicationStatus status) {
        this.status = status;
    }

    public Instant getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Instant startedDate) {
        this.startedDate = startedDate;
    }

    public Instant getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Instant stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public String getIndicatedFor() {
        return indicatedFor;
    }

    public void setIndicatedFor(String indicatedFor) {
        this.indicatedFor = indicatedFor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDosageQuantity() {
        return dosageQuantity;
    }

    public void setDosageQuantity(String dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }
}
