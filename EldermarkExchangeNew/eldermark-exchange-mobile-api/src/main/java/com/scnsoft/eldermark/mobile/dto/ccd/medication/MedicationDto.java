package com.scnsoft.eldermark.mobile.dto.ccd.medication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.ClientMedicationStatus;
import com.scnsoft.eldermark.mobile.dto.ccd.DataSource;

public class MedicationDto extends BaseMedicationDto {

    private String ndcName;
    private String ndcCode;
    private String mediSpanId;
    @JsonIgnore
    private Long clientId;
    private Integer prescriptionQuantity;
    private Long prescribedDate;
    private Long prescriptionExpirationDate;
    private ClientMedicationStatus status;
    private String comment;
    private String strength;
    private String doseForm;
    private String dosageQuantity;
    private String route;
    private String statusName;
    private String statusTitle;
    private Long refillDate;
    private String frequency;
    private String recurrence;
    private String directions;
    private String indicatedFor;
    private PrescribedBy prescribedBy;
    private Pharmacy dispensingPharmacy;
    private Pharmacy pharmacy;
    private String pharmRxId;
    private Long pharmacyOriginDate;
    private Long stopDeliveryAfterDate;
    private Long lastUpdate;
    private String lastUpdateStr;
    private String origin;
    private DataSource dataSource;
    private Long recordedDate;
    private String recordedByName;
    private Long editedDate;
    private String editedByName;

    public String getNdcName() {
        return ndcName;
    }

    public void setNdcName(String ndcName) {
        this.ndcName = ndcName;
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

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
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

    public Pharmacy getDispensingPharmacy() {
        return dispensingPharmacy;
    }

    public void setDispensingPharmacy(Pharmacy dispensingPharmacy) {
        this.dispensingPharmacy = dispensingPharmacy;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getPharmRxId() {
        return pharmRxId;
    }

    public void setPharmRxId(String pharmRxId) {
        this.pharmRxId = pharmRxId;
    }

    public Long getPharmacyOriginDate() {
        return pharmacyOriginDate;
    }

    public void setPharmacyOriginDate(Long pharmacyOriginDate) {
        this.pharmacyOriginDate = pharmacyOriginDate;
    }

    public Long getStopDeliveryAfterDate() {
        return stopDeliveryAfterDate;
    }

    public void setStopDeliveryAfterDate(Long stopDeliveryAfterDate) {
        this.stopDeliveryAfterDate = stopDeliveryAfterDate;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateStr() {
        return lastUpdateStr;
    }

    public void setLastUpdateStr(String lastUpdateStr) {
        this.lastUpdateStr = lastUpdateStr;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getNdcCode() {
        return ndcCode;
    }

    public void setNdcCode(String ndcCode) {
        this.ndcCode = ndcCode;
    }

    public String getMediSpanId() {
        return mediSpanId;
    }

    public void setMediSpanId(String mediSpanId) {
        this.mediSpanId = mediSpanId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getPrescriptionQuantity() {
        return prescriptionQuantity;
    }

    public void setPrescriptionQuantity(Integer prescriptionQuantity) {
        this.prescriptionQuantity = prescriptionQuantity;
    }

    public Long getPrescribedDate() {
        return prescribedDate;
    }

    public void setPrescribedDate(Long prescribedDate) {
        this.prescribedDate = prescribedDate;
    }

    public Long getPrescriptionExpirationDate() {
        return prescriptionExpirationDate;
    }

    public void setPrescriptionExpirationDate(Long prescriptionExpirationDate) {
        this.prescriptionExpirationDate = prescriptionExpirationDate;
    }

    public ClientMedicationStatus getStatus() {
        return status;
    }

    public void setStatus(ClientMedicationStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDoseForm() {
        return doseForm;
    }

    public void setDoseForm(String doseForm) {
        this.doseForm = doseForm;
    }

    public String getDosageQuantity() {
        return dosageQuantity;
    }

    public void setDosageQuantity(String dosageQuantity) {
        this.dosageQuantity = dosageQuantity;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public void setEditedByName(String editedByName) {
        this.editedByName = editedByName;
    }

    public String getEditedByName() {
        return editedByName;
    }

    public static class PrescribedBy {
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

    public static class Pharmacy {
        private String code;
        private String name;
        private String phone;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
