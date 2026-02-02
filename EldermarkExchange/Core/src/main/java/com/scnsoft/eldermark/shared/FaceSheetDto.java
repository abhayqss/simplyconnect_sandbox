package com.scnsoft.eldermark.shared;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FaceSheetDto {
    private String medicalRecordNumber;
    private String residentName;
    private String preferredName;
    private Date dob;
    private String age;
    private String gender;
    private String religion;
    private String maritalStatus;
    private String race;
    private String primaryLanguage;
    private String unit;
    private String veteran;
    private Date admissionDate;
    private String homePhone;
    private String previousAddress;
    private Date readmissionDate;
    private Date startOfCare;
    private List<FaceSheetDto.MedicalProfessional> medicalProfessional;
    private String pharmacy;
    private String hospitalPref;
    private String transportation;
    private String ambulance;
    private String evacuationStatus;
    private List<Contact> responsibleParty;
    private String preAdmissionNumber;
    private String primaryPayType;
    private String ssn;
    private String medicareNumber;
    private String medicaidNumber;
    private String authorizationNumber;
    private Date authExpDate;
    private String healthPlanNumber;
    private String dentalPlanNumber;
    private String companyName;
    private String companyAddress1;
    private String companyAddress2;
    private String companyPhone;
    private String companyFax;
    private Date faceSheetPrintedTime;
    private String otherPhone;
    private String email;
    private String admittedFrom;
    private String countyAdmittedFrom;
    private List<Contact> contactList;
    private String notesAlerts;
    private List<Allergy> allergies;
    private List<Diagnosis> diagnosis;
    private List<Order> orders;
    private List<Note> notes;
    private List<AdvanceDirective> advanceDirectives;

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public Date getReadmissionDate() {
        return readmissionDate;
    }

    public void setReadmissionDate(Date readmissionDate) {
        this.readmissionDate = readmissionDate;
    }

    public Date getStartOfCare() {
        return startOfCare;
    }

    public void setStartOfCare(Date startOfCare) {
        this.startOfCare = startOfCare;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public String getAdmittedFrom() {
        return admittedFrom;
    }

    public void setAdmittedFrom(String admittedFrom) {
        this.admittedFrom = admittedFrom;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getPreviousAddress() {
        return previousAddress;
    }

    public void setPreviousAddress(String previousAddress) {
        this.previousAddress = previousAddress;
    }

    public List<FaceSheetDto.MedicalProfessional> getMedicalProfessional() {
        return medicalProfessional;
    }

    public void setMedicalProfessional(List<FaceSheetDto.MedicalProfessional> medicalProfessional) {
        this.medicalProfessional = medicalProfessional;
    }

    public String getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getHospitalPref() {
        return hospitalPref;
    }

    public void setHospitalPref(String hospitalPref) {
        this.hospitalPref = hospitalPref;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getAmbulance() {
        return ambulance;
    }

    public void setAmbulance(String ambulance) {
        this.ambulance = ambulance;
    }

    public String getEvacuationStatus() {
        return evacuationStatus;
    }

    public void setEvacuationStatus(String evacuationStatus) {
        this.evacuationStatus = evacuationStatus;
    }

    public List<Contact> getResponsibleParty() {
        return responsibleParty;
    }

    public void setResponsibleParty(List<Contact> responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    public String getPreAdmissionNumber() {
        return preAdmissionNumber;
    }

    public void setPreAdmissionNumber(String preAdmissionNumber) {
        this.preAdmissionNumber = preAdmissionNumber;
    }

    public String getPrimaryPayType() {
        return primaryPayType;
    }

    public void setPrimaryPayType(String primaryPayType) {
        this.primaryPayType = primaryPayType;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getAuthorizationNumber() {
        return authorizationNumber;
    }

    public void setAuthorizationNumber(String authorizationNumber) {
        this.authorizationNumber = authorizationNumber;
    }

    public Date getAuthExpDate() {
        return authExpDate;
    }

    public void setAuthExpDate(Date authExpDate) {
        this.authExpDate = authExpDate;
    }

    public String getHealthPlanNumber() {
        return healthPlanNumber;
    }

    public void setHealthPlanNumber(String healthPlanNumber) {
        this.healthPlanNumber = healthPlanNumber;
    }

    public String getDentalPlanNumber() {
        return dentalPlanNumber;
    }

    public void setDentalPlanNumber(String dentalPlanNumber) {
        this.dentalPlanNumber = dentalPlanNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public Date getFaceSheetPrintedTime() {
        return faceSheetPrintedTime;
    }

    public void setFaceSheetPrintedTime(Date faceSheetPrintedTime) {
        this.faceSheetPrintedTime = faceSheetPrintedTime;
    }

    public String getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountyAdmittedFrom() {
        return countyAdmittedFrom;
    }

    public void setCountyAdmittedFrom(String countyAdmittedFrom) {
        this.countyAdmittedFrom = countyAdmittedFrom;

    }

    public String getNotesAlerts() {
        return notesAlerts;
    }

    public void setNotesAlerts(String notesAlerts) {
        this.notesAlerts = notesAlerts;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    public List<Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(List<Diagnosis> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<AdvanceDirective> getAdvanceDirectives() {
        return advanceDirectives;
    }

    public void setAdvanceDirectives(List<AdvanceDirective> advanceDirectives) {
        this.advanceDirectives = advanceDirectives;
    }

    public class Contact implements Comparable<Contact> {
        private String name;
        private String address1;
        private String address2;
        private String relationship;
        private String phone;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Contact)) return false;
            Contact contact = (Contact) o;
            return Objects.equals(getName(), contact.getName()) &&
                    Objects.equals(getAddress1(), contact.getAddress1()) &&
                    Objects.equals(getAddress2(), contact.getAddress2()) &&
                    Objects.equals(getRelationship(), contact.getRelationship()) &&
                    Objects.equals(getPhone(), contact.getPhone());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getAddress1(), getAddress2(), getRelationship(), getPhone());
        }



        public Contact() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        @Override
        public int compareTo(Contact o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getName(), o.getName());
        }
    }

    public class MedicalProfessional {
        private String role;
        private String data;

        //this includes only data relate dto person in order to identify duplicates
        private String personRelatedData;

        public MedicalProfessional() {}

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getPersonRelatedData() {
            return personRelatedData;
        }

        public void setPersonRelatedData(String personRelatedData) {
            this.personRelatedData = personRelatedData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MedicalProfessional)) return false;
            MedicalProfessional that = (MedicalProfessional) o;
            return Objects.equals(getPersonRelatedData(), that.getPersonRelatedData());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPersonRelatedData());
        }
    }

    public class Allergy implements Comparable<Allergy> {
        String substance;
        String type;
        String reaction;
        Date startDate;
        String dataSource;

        public String getSubstance() {
            return substance;
        }

        public void setSubstance(String substance) {
            this.substance = substance;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReaction() {
            return reaction;
        }

        public void setReaction(String reaction) {
            this.reaction = reaction;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public int compareTo(Allergy o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getStartDate(), o.getStartDate());
        }
    }

    public class Diagnosis implements Comparable<Diagnosis> {
        private String diagnosis;
        private String code;
        private String codeSet;
        private String type;
        private Date identified;
        private String dataSource;

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCodeSet() {
            return codeSet;
        }

        public void setCodeSet(String codeSet) {
            this.codeSet = codeSet;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getIdentified() {
            return identified;
        }

        public void setIdentified(Date identified) {
            this.identified = identified;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public int compareTo(Diagnosis o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getIdentified(), o.getIdentified());
        }
    }

    public class Order implements Comparable<Order> {
        private String name;
        private Date startDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        @Override
        public int compareTo(Order o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getStartDate(), o.getStartDate());
        }
    }

    public class Note implements Comparable<Note> {
        private Date date;
        private String type;
        private String note;
        private String subjective;
        private String objective;
        private String assessment;
        private String plan;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getSubjective() {
            return subjective;
        }

        public void setSubjective(String subjective) {
            this.subjective = subjective;
        }

        public String getObjective() {
            return objective;
        }

        public void setObjective(String objective) {
            this.objective = objective;
        }

        public String getAssessment() {
            return assessment;
        }

        public void setAssessment(String assessment) {
            this.assessment = assessment;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        @Override
        public int compareTo(Note o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getDate(), o.getDate());
        }
    }

    public class AdvanceDirective implements Comparable<AdvanceDirective> {
        private String type;
        private String code;
        private String codeSet;
        private String verification;
        private Date dateStarted;
        private String dataSource;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCodeSet() {
            return codeSet;
        }

        public void setCodeSet(String codeSet) {
            this.codeSet = codeSet;
        }

        public String getVerification() {
            return verification;
        }

        public void setVerification(String verification) {
            this.verification = verification;
        }

        public Date getDateStarted() {
            return dateStarted;
        }

        public void setDateStarted(Date dateStarted) {
            this.dateStarted = dateStarted;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AdvanceDirective that = (AdvanceDirective) o;

            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (code != null ? !code.equals(that.code) : that.code != null) return false;
            if (codeSet != null ? !codeSet.equals(that.codeSet) : that.codeSet != null) return false;
            if (verification != null ? !verification.equals(that.verification) : that.verification != null)
                return false;
            if (dateStarted != null ? !dateStarted.equals(that.dateStarted) : that.dateStarted != null) return false;
            return dataSource != null ? dataSource.equals(that.dataSource) : that.dataSource == null;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (code != null ? code.hashCode() : 0);
            result = 31 * result + (codeSet != null ? codeSet.hashCode() : 0);
            result = 31 * result + (verification != null ? verification.hashCode() : 0);
            result = 31 * result + (dateStarted != null ? dateStarted.hashCode() : 0);
            result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(AdvanceDirective o) {
            if (o == null) {
                return 0;
            }
            return ObjectUtils.compare(getDateStarted(), o.getDateStarted());
        }
    }
}
