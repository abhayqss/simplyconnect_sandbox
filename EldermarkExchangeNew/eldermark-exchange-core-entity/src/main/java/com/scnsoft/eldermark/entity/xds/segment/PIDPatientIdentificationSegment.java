package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0001AdministrativeSex;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0136YesNoIndicator;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PID_PatientIdentificationSegment")
public class PIDPatientIdentificationSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PatientAlias_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "patient_alias_id"))
    private List<XPNPersonName> patientAliases;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patientID_id")
    private CXExtendedCompositeId patientID;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PatientIdentifier_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "patient_identifier_id"))
    private List<CXExtendedCompositeId> patientIdentifiers;

    @Column(name = "datetime_of_birth", columnDefinition = "datetime2")
    private LocalDate dateTimeOfBirth;

    @Column(name = "ssn_number_patient")
    private String ssnNumberPatient;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "primary_language_id")
    private CECodedElement primaryLanguage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "marital_status_id")
    private CECodedElement maritalStatus;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "religion_id")
    private CECodedElement religion;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_account_number_id")
    private CXExtendedCompositeId patientAccountNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_Race_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "race_id"))
    private List<CECodedElement> races;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_EthnicGroup_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "ethnic_group_id"))
    private List<CECodedElement> ethnicGroups;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nationality_id")
    private CECodedElement nationality;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_Citizenship_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "citizenship_id"))
    private List<CECodedElement> citizenships;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "veterans_military_status_id")
    private CECodedElement veteransMilitaryStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PhoneNumberHome_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "phone_number_home_id"))
    private List<XTNPhoneNumber> phoneNumbersHome;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PhoneNumberBusiness_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "phone_number_business_id"))
    private List<XTNPhoneNumber> phoneNumbersBusiness;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PatientAddress_LIST", joinColumns = @JoinColumn(name = "pid_id"), inverseJoinColumns = @JoinColumn(name = "patient_address_id"))
    private List<XADPatientAddress> patientAddresses;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "administrative_sex_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> administrativeSex;

    @Column(name = "patient_death_date_and_time")
    private Instant patientDeathDateAndTime;

    @Column(name = "birth_place", columnDefinition = "nvarchar(255)")
    @Nationalized
    private String birthPlace;

    @Column(name = "birth_order")
    private Integer birthOrder;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_death_indicator_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> patientDeathIndicator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "drivers_license_number_patient_id")
    private DLNDriverSLicenseNumber driversLicenseNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_MothersMaidenName_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "mothers_maiden_name_id"))
    private List<XPNPersonName> mothersMaidenNames;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_PatientName_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_name_id"))
    private List<XPNPersonName> patientNames;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ADT_FIELD_PID_MothersIdentifier_LIST",
            joinColumns = @JoinColumn(name = "pid_id"),
            inverseJoinColumns = @JoinColumn(name = "mothers_identifier_id"))
    private List<CXExtendedCompositeId> motherIdentifiers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "species_code_id")
    private CECodedElement speciesCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<XPNPersonName> getPatientAliases() {
        return patientAliases;
    }

    public void setPatientAliases(List<XPNPersonName> patientAliases) {
        this.patientAliases = patientAliases;
    }

    public CXExtendedCompositeId getPatientID() {
        return patientID;
    }

    public void setPatientID(CXExtendedCompositeId patientID) {
        this.patientID = patientID;
    }

    public List<CXExtendedCompositeId> getPatientIdentifiers() {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers(List<CXExtendedCompositeId> patientIdentifiers) {
        this.patientIdentifiers = patientIdentifiers;
    }

    public LocalDate getDateTimeOfBirth() {
        return dateTimeOfBirth;
    }

    public void setDateTimeOfBirth(LocalDate dateTimeOfBirth) {
        this.dateTimeOfBirth = dateTimeOfBirth;
    }

    public String getSsnNumberPatient() {
        return ssnNumberPatient;
    }

    public void setSsnNumberPatient(String ssnNumberPatient) {
        this.ssnNumberPatient = ssnNumberPatient;
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

    public CECodedElement getReligion() {
        return religion;
    }

    public void setReligion(CECodedElement religion) {
        this.religion = religion;
    }

    public CXExtendedCompositeId getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(CXExtendedCompositeId patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public List<CECodedElement> getRaces() {
        return races;
    }

    public void setRaces(List<CECodedElement> races) {
        this.races = races;
    }

    public List<CECodedElement> getEthnicGroups() {
        return ethnicGroups;
    }

    public void setEthnicGroups(List<CECodedElement> ethnicGroups) {
        this.ethnicGroups = ethnicGroups;
    }

    public CECodedElement getNationality() {
        return nationality;
    }

    public void setNationality(CECodedElement nationality) {
        this.nationality = nationality;
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

    public List<XADPatientAddress> getPatientAddresses() {
        return patientAddresses;
    }

    public void setPatientAddresses(List<XADPatientAddress> patientAddresses) {
        this.patientAddresses = patientAddresses;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> getAdministrativeSex() {
        return administrativeSex;
    }

    public void setAdministrativeSex(
            ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> administrativeSex) {
        this.administrativeSex = administrativeSex;
    }

    public Instant getPatientDeathDateAndTime() {
        return patientDeathDateAndTime;
    }

    public void setPatientDeathDateAndTime(Instant patientDeathDateAndTime) {
        this.patientDeathDateAndTime = patientDeathDateAndTime;
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

    public IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> getPatientDeathIndicator() {
        return patientDeathIndicator;
    }

    public void setPatientDeathIndicator(IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator> patientDeathIndicator) {
        this.patientDeathIndicator = patientDeathIndicator;
    }

    public DLNDriverSLicenseNumber getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(DLNDriverSLicenseNumber driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public List<XPNPersonName> getMothersMaidenNames() {
        return mothersMaidenNames;
    }

    public void setMothersMaidenNames(List<XPNPersonName> mothersMaidenNames) {
        this.mothersMaidenNames = mothersMaidenNames;
    }

    public List<XPNPersonName> getPatientNames() {
        return patientNames;
    }

    public void setPatientNames(List<XPNPersonName> patientNames) {
        this.patientNames = patientNames;
    }

    public List<CXExtendedCompositeId> getMotherIdentifiers() {
        return motherIdentifiers;
    }

    public void setMotherIdentifiers(List<CXExtendedCompositeId> motherIdentifiers) {
        this.motherIdentifiers = motherIdentifiers;
    }

    public CECodedElement getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(CECodedElement speciesCode) {
        this.speciesCode = speciesCode;
    }
}
