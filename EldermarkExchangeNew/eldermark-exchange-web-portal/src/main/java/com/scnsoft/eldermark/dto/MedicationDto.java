package com.scnsoft.eldermark.dto;

import java.util.List;

public class MedicationDto extends MedicationListItemDto {
    private String ndc;
    private String statusName;
    private String statusTitle;
    private Long refillDate;
    private String frequency;
    private String recurrence;
    private List<String> indications;
    private String indicatedFor;
    private PrescribedBy prescribedBy;
    private String dispensingPharmacyCode;
    private String dispensingPharmacyName;
    private String dispensingPharmacyPhone;
    private String pharmRxid;
    private Long pharmacyOriginDate;
    private Long endDateFuture;
    private String origin;
    private String organizationName;
    private String communityName;
    private String lastUpdate;
    private Long stopDeliveryAfterDate;
    private String pharmacyCode;
    private String pharmacyName;
    private String pharmacyPhone;
    private String dosageQuantity;
    private Long prescribedDate;
    private Integer prescriptionQuantity;
    private Long prescriptionExpirationDate;
    private Long recordedDate;
    private String recordedByName;
    private Long editedDate;
    private String editedByName;
    private String comment;

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getRefillDate() {
        return refillDate;
    }

    public void setRefillDate(Long refillDate) {
        this.refillDate = refillDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public List<String> getIndications() {
        return indications;
    }

    public void setIndications(List<String> indications) {
        this.indications = indications;
    }

    public String getIndicatedFor() {
        return indicatedFor;
    }

    public void setIndicatedFor(String indicatedFor) {
        this.indicatedFor = indicatedFor;
    }

    public PrescribedBy getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(PrescribedBy prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getDispensingPharmacyCode() {
        return dispensingPharmacyCode;
    }

    public void setDispensingPharmacyCode(String dispensingPharmacyCode) {
        this.dispensingPharmacyCode = dispensingPharmacyCode;
    }

    public String getDispensingPharmacyName() {
        return dispensingPharmacyName;
    }

    public void setDispensingPharmacyName(String dispensingPharmacyName) {
        this.dispensingPharmacyName = dispensingPharmacyName;
    }

    public String getDispensingPharmacyPhone() {
        return dispensingPharmacyPhone;
    }

    public void setDispensingPharmacyPhone(String dispensingPharmacyPhone) {
        this.dispensingPharmacyPhone = dispensingPharmacyPhone;
    }

    public String getPharmRxid() {
        return pharmRxid;
    }

    public void setPharmRxid(String pharmRxid) {
        this.pharmRxid = pharmRxid;
    }

    public Long getPharmacyOriginDate() {
        return pharmacyOriginDate;
    }

    public void setPharmacyOriginDate(Long pharmacyOriginDate) {
        this.pharmacyOriginDate = pharmacyOriginDate;
    }

    public Long getEndDateFuture() {
        return endDateFuture;
    }

    public void setEndDateFuture(Long endDateFuture) {
        this.endDateFuture = endDateFuture;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getStopDeliveryAfterDate() {
        return stopDeliveryAfterDate;
    }

    public void setStopDeliveryAfterDate(Long stopDeliveryAfterDate) {
        this.stopDeliveryAfterDate = stopDeliveryAfterDate;
    }

    public String getPharmacyCode() {
        return pharmacyCode;
    }

    public void setPharmacyCode(String pharmacyCode) {
        this.pharmacyCode = pharmacyCode;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    public String getDosageQuantity() {
        return dosageQuantity;
    }

    public void setDosageQuantity(String dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public Long getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(Long prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public Integer getPrescriptionQuantity() {
        return prescriptionQuantity;
    }

    public void setPrescriptionQuantity(Integer prescriptionQuantity) {
        this.prescriptionQuantity = prescriptionQuantity;
    }

    public Long getPrescriptionExpirationDate() {
        return prescriptionExpirationDate;
    }

    public void setPrescriptionExpirationDate(Long prescriptionExpirationDate) {
        this.prescriptionExpirationDate = prescriptionExpirationDate;
    }

    public Long getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Long recordedDate) {
        this.recordedDate = recordedDate;
    }

    public String getRecordedByName() {
        return recordedByName;
    }

    public void setRecordedByName(String recordedByName) {
        this.recordedByName = recordedByName;
    }

    public Long getEditedDate() {
        return editedDate;
    }

    public void setEditedDate(Long editedDate) {
        this.editedDate = editedDate;
    }

    public String getEditedByName() {
        return editedByName;
    }

    public void setEditedByName(String editedByName) {
        this.editedByName = editedByName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public class PrescribedBy {
        private String code;
        private String firstName;
        private String lastName;
        private String speciality;
        private String workPhone;
        private String email;
        private String organizationName;
        private String communityName;
        private String address;
        private String extPharmacyId;
        private String npi;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

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

        public String getSpeciality() {
            return speciality;
        }

        public void setSpeciality(String speciality) {
            this.speciality = speciality;
        }

        public String getWorkPhone() {
            return workPhone;
        }

        public void setWorkPhone(String workPhone) {
            this.workPhone = workPhone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getExtPharmacyId() {
            return extPharmacyId;
        }

        public void setExtPharmacyId(String extPharmacyId) {
            this.extPharmacyId = extPharmacyId;
        }

        public String getNpi() {
            return npi;
        }

        public void setNpi(String npi) {
            this.npi = npi;
        }
    }
}
