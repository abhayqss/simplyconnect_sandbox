package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0001AdministrativeSex;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0136YesNoIndicator;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PID_PatientIdentificationSegment")
public class PIDPatientIdentificationSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PatientIdentifier_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_identifier_id")
    )
    private List<CXExtendedCompositeId> patientIdentifiers;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PatientName_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_name_id")
    )
    private List<XPNPersonName> patientNames;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_MothersMaidenName_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "mothers_maiden_name_id")
    )
    private List<XPNPersonName> mothersMaidenNames;

    @Column(name = "datetime_of_birth")
    private Date dateTimeOfBirth;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "administrative_sex_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> administrativeSex;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PatientAlias_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_alias_id")
    )
    private List<XPNPersonName> patientAliases;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_Race_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "race_id")
    )
    private List<CECodedElement> races;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PatientAddress_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_address_id")
    )
    private List<XADPatientAddress> patientAddresses;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PhoneNumberHome_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "phone_number_home_id")
    )
    private List<XTNPhoneNumber> phoneNumbersHome;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_PhoneNumberBusiness_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "phone_number_business_id")
    )
    private List<XTNPhoneNumber> phoneNumbersBusiness;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "primary_language_id")
    private CECodedElement primaryLanguage;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "marital_status_id")
    private CECodedElement maritalStatus;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "religion_id")
    private CECodedElement religion;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "patient_account_number_id")
    private CXExtendedCompositeId patientAccountNumber;

    @Column(name = "ssn_number_patient")
    private String ssnNumberPatient;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "drivers_license_number_patient_id")
    private DLNDriverSLicenseNumber driversLicenseNumber;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_MothersIdentifier_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "mothers_identifier_id")
    )
    private List<CXExtendedCompositeId> mothersIdentifiers;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_EthnicGroup_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "ethnic_group_id")
    )
    private List<CECodedElement> ethnicGroups;

    @Nationalized
    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "birth_order")
    private Integer birthOrder;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_FIELD_PID_Citizenship_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "citizenship_id")
    )
    private List<CECodedElement> citizenships;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "veterans_military_status_id")
    private CECodedElement veteransMilitaryStatus;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "nationality_id")
    private CECodedElement nationality;

    @Column(name = "patient_death_date_and_time")
    private Date patientDeathDateAndTime;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "patient_death_indicator_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> patientDeathIndicator;

    public ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> getAdministrativeSex() {
        return administrativeSex;
    }

    public void setAdministrativeSex(ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> administrativeSex) {
        this.administrativeSex = administrativeSex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CXExtendedCompositeId> getPatientIdentifiers() {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers(List<CXExtendedCompositeId> patientIdentifiers) {
        this.patientIdentifiers = patientIdentifiers;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<XPNPersonName> getPatientNames() {
        return patientNames;
    }

    public void setPatientNames(List<XPNPersonName> patientNames) {
        this.patientNames = patientNames;
    }

    public List<XPNPersonName> getMothersMaidenNames() {
        return mothersMaidenNames;
    }

    public void setMothersMaidenNames(List<XPNPersonName> mothersMaidenNames) {
        this.mothersMaidenNames = mothersMaidenNames;
    }

    public List<XPNPersonName> getPatientAliases() {
        return patientAliases;
    }

    public void setPatientAliases(List<XPNPersonName> patientAliases) {
        this.patientAliases = patientAliases;
    }

    public List<CECodedElement> getRaces() {
        return races;
    }

    public void setRaces(List<CECodedElement> races) {
        this.races = races;
    }

    public List<XADPatientAddress> getPatientAddresses() {
        return patientAddresses;
    }

    public void setPatientAddresses(List<XADPatientAddress> patientAddresses) {
        this.patientAddresses = patientAddresses;
    }

    public List<XTNPhoneNumber> getPhoneNumbersHome() {
        return phoneNumbersHome;
    }

    public void setPhoneNumbersHome(List<XTNPhoneNumber> phoneNumbersHome) {
        this.phoneNumbersHome = phoneNumbersHome;
    }

    public List<XTNPhoneNumber> getPhoneNumbersBusiness() {
        return phoneNumbersBusiness;
    }

    public void setPhoneNumbersBusiness(List<XTNPhoneNumber> phoneNumbersBusiness) {
        this.phoneNumbersBusiness = phoneNumbersBusiness;
    }

    public CECodedElement getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(CECodedElement primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public CECodedElement getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CECodedElement maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public CXExtendedCompositeId getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(CXExtendedCompositeId patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public String getSsnNumberPatient() {
        return ssnNumberPatient;
    }

    public void setSsnNumberPatient(String ssnNumberPatient) {
        this.ssnNumberPatient = ssnNumberPatient;
    }

    public DLNDriverSLicenseNumber getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(DLNDriverSLicenseNumber driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public List<CXExtendedCompositeId> getMothersIdentifiers() {
        return mothersIdentifiers;
    }

    public void setMothersIdentifiers(List<CXExtendedCompositeId> mothersIdentifiers) {
        this.mothersIdentifiers = mothersIdentifiers;
    }

    public List<CECodedElement> getEthnicGroups() {
        return ethnicGroups;
    }

    public void setEthnicGroups(List<CECodedElement> ethnicGroups) {
        this.ethnicGroups = ethnicGroups;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Integer getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(Integer birthOrder) {
        this.birthOrder = birthOrder;
    }

    public List<CECodedElement> getCitizenships() {
        return citizenships;
    }

    public void setCitizenships(List<CECodedElement> citizenships) {
        this.citizenships = citizenships;
    }

    public CECodedElement getVeteransMilitaryStatus() {
        return veteransMilitaryStatus;
    }

    public void setVeteransMilitaryStatus(CECodedElement veteransMilitaryStatus) {
        this.veteransMilitaryStatus = veteransMilitaryStatus;
    }

    public CECodedElement getNationality() {
        return nationality;
    }

    public void setNationality(CECodedElement nationality) {
        this.nationality = nationality;
    }

    public Date getPatientDeathDateAndTime() {
        return patientDeathDateAndTime;
    }

    public void setPatientDeathDateAndTime(Date patientDeathDateAndTime) {
        this.patientDeathDateAndTime = patientDeathDateAndTime;
    }

    public Date getDateTimeOfBirth() {
        return dateTimeOfBirth;
    }

    public void setDateTimeOfBirth(Date dateTimeOfBirth) {
        this.dateTimeOfBirth = dateTimeOfBirth;
    }

    public CECodedElement getReligion() {
        return religion;
    }

    public void setReligion(CECodedElement religion) {
        this.religion = religion;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> getPatientDeathIndicator() {
        return patientDeathIndicator;
    }

    public void setPatientDeathIndicator(IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> patientDeathIndicator) {
        this.patientDeathIndicator = patientDeathIndicator;
    }

//    public enum SexType {
//        MALE("Male", "M"),
//        FEMALE("Female", "F"),
//        OTHER("Other", "O"),
//        UNKNOWN("Unknown", "U");
//
//        private String value = null;
//        private String cdaValue = null;
//
//        SexType(String value, String cdaValue) {
//            this.value = value;
//            this.cdaValue = cdaValue;
//        }
//
//        public static SexType getSexByCode(String code) {
//            Validate.notNull(code, "code cannot be null");
//
//            Gender gender = null;
//            for (Gender g: Gender.values()) {
//                if (code.equals(g.getAdministrativeGenderCode())) {
//                    gender = g;
//                    break;
//                }
//            }
//
//            if (gender == null) {
//                throw new IllegalArgumentException("Gender with code '" + code + "' not found");
//            }
//
//            return gender;
//        }
//    }
}
