package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Access(AccessType.FIELD)
@Table(indexes = { @Index(name = "IX_resident_custodian", columnList = "custodian_id"),
        @Index(name = "IX_resident_database", columnList = "database_id"),
        @Index(name = "IX_resident_ssn_hash", columnList = "ssn_hash"),
        @Index(name = "IX_resident_legacy_id", columnList = "legacy_id"),
        @Index(name = "IX_resident_birthdate_hash_facility_opt_out", columnList = "birth_date_hash, facility_id, opt_out"),
        @Index(name = "ix_facility", columnList = "facility_id") }, uniqueConstraints = @UniqueConstraint(columnNames = {
                "legacy_id", "database_id" }))
@AttributeOverrides({
        @AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25)),
        @AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = true, length = 100)) })
@NamedQueries({
        @NamedQuery(name = "resident.getPatientOrganizationIdsForEmployee", query = "SELECT DISTINCT r.facility.id "
                + "FROM Resident r LEFT JOIN r.residentCareTeamMembers rct WHERE rct.residentId = r.id "
                + "AND (rct.employee.id = :employeeId OR r.createdById = :employeeId)") })
/**
 * IMPORTANT: when adding a new field to entity,
 * ResidentDaoImpl->getGroupByForResident method should be updated (add to GROUP
 * BY).
 */
public class Resident extends StringLegacyTableAwareEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "admit_date")
    private Date admitDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "discharge_date")
    private Date dischargeDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "birth_date_hash", insertable = false, updatable = false)
    private Long birthDateHash;

    @Column(name = "birth_place", length = 500)
    private String birthPlace;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident", optional = true)
    private BirthplaceAddress birthplaceAddress;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    @Column(name = "age")
    private Integer age;

    @ManyToOne
    @JoinColumn
    private CcdCode gender;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @ManyToOne
    @JoinColumn(name = "ethnic_group_id")
    private CcdCode ethnicGroup;

    @ManyToOne
    @JoinColumn
    private CcdCode religion;

    @Column(name = "ssn", length = 11)
    private String socialSecurity;

    @Column(name = "ssn_hash", insertable = false, updatable = false)
    private Long socialSecurityHash;

    @Column(name = "ssn_last_four_digits", length = 4, insertable = false, updatable = false)
    private String ssnLastFourDigits;

    @ManyToOne
    @JoinColumn
    private CcdCode race;

    @Column(name = "opt_out")
    private Boolean isOptOut;

    // (DE)ACTIVATED in Care Coordination
    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<Guardian> guardians;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<Language> languages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<DocumentationOf> documentationOfs;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<AdvanceDirective> advanceDirectives;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<Author> authors;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_organization_id")
    private Organization providerOrganization;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data_enterer_id")
    private DataEnterer dataEnterer;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "custodian_id")
    private Custodian custodian;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "legal_authenticator_id")
    private LegalAuthenticator legalAuthenticator;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "survivingResident")
    private Set<MpiMergedResidents> secondaryResidents;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mergedResident")
    private Set<MpiMergedResidents> mainResidents;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH }, mappedBy = "resident")
    private Set<MPI> mpi;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<ResidentDevice> devices;

    /**
     * IDs of residents that were marked as "mismatching" with this Resident
     * during manual matching procedure
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "MPI_unmerged_residents", joinColumns = @JoinColumn(name = "first_resident_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "first_resident_id", "second_resident_id" }))
    @Column(name = "second_resident_id", nullable = false)
    private Set<Long> unmergedResidentIds;

    @Column(name = "hash_key", updatable = false, insertable = false)
    private String hashKey;

    @Column(name = "medical_record_number")
    private String medicalRecordNumber;

    @Column(name = "veteran")
    private String veteran;

    @Column(name = "prev_addr_street") // PersonAddress
    private String prevAddrStreet;

    @Column(name = "prev_addr_city")
    private String prevAddrCity;

    @Column(name = "prev_addr_state")
    private String prevAddrState;

    @Column(name = "prev_addr_zip")
    private String prevAddrZip;

    @Column(name = "hospital_of_preference")
    private String hospitalPreference;

    @Column(name = "transportation_preference")
    private String transportationPreference;

    @Column(name = "ambulance_preference")
    private String ambulancePreference;

    @Column(name = "preadmission_number")
    private String preadmissionNumber;

    @Column(name = "medicare_number")
    private String medicareNumber;

    @Column(name = "ma_authorization_number")
    private String authorizationNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ma_auth_numb_expire_date")
    private Date authorizationNumberExpires;

    @Column(name = "medicaid_number")
    private String medicaidNumber;

    @Column(name = "evacuation_status")
    private String evacuationStatus;

    @Column(name = "unit_number")
    private String unitNumber;

    @Column(name = "advance_directive_free_text", columnDefinition = "text")
    private String advanceDirectiveFreeText;

    @Column(name = "Dental_Insurance", columnDefinition = "text")
    private String dentalInsurance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<ResidentOrder> orders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<ResidentNotes> alertNotes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<ResidentHealthPlan> healthPlans;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "resident", fetch = FetchType.LAZY)
    private List<ResidentCareTeamMember> residentCareTeamMembers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<Event> events;

    @Column(name = "date_created")
    private Date dateCreated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<Participant> participants;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "intake_date")
    private Date intakeDate;

    @Column(name = "current_pharmacy_name")
    private String currentPharmacyName;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "member_number")
    private String memberNumber;

    @Column(name = "consana_xref_id")
    private String consanaXrefId;

    // We are using accessor type on properties
    // so we put @Transient annotation to tell JPA not to use these fields for
    // storing
    @Transient
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private String middleName;

    @Transient
    private String preferredName;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "citizenship", length = 100)
    private String citizenship;

    @Column(name = "insurance_plan")
    private String insurancePlanName;

    public Resident() {
    }

    public Resident(Long id) {
        super(id);
    }

    public Date getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Date admitDate) {
        this.admitDate = admitDate;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Long getBirthDateHash() {
        return birthDateHash;
    }

    public void setBirthDateHash(Long birthDateHash) {
        this.birthDateHash = birthDateHash;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public BirthplaceAddress getBirthplaceAddress() {
        return birthplaceAddress;
    }

    public void setBirthplaceAddress(BirthplaceAddress birthplaceAddress) {
        this.birthplaceAddress = birthplaceAddress;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public CcdCode getReligion() {
        return religion;
    }

    public void setReligion(CcdCode religion) {
        this.religion = religion;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public Long getSocialSecurityHash() {
        return socialSecurityHash;
    }

    public void setSocialSecurityHash(Long socialSecurityHash) {
        this.socialSecurityHash = socialSecurityHash;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Guardian> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<Guardian> guardians) {
        this.guardians = guardians;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Boolean isOptOut() {
        return isOptOut != null ? isOptOut : true;
    }

    public void setOptOut(Boolean optOut) {
        isOptOut = optOut;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Organization getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(Organization providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public CcdCode getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(CcdCode ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public List<DocumentationOf> getDocumentationOfs() {
        return documentationOfs;
    }

    public void setDocumentationOfs(List<DocumentationOf> documentationOfs) {
        this.documentationOfs = documentationOfs;
    }

    public List<AdvanceDirective> getAdvanceDirectives() {
        return advanceDirectives;
    }

    public void setAdvanceDirectives(List<AdvanceDirective> advanceDirectives) {
        this.advanceDirectives = advanceDirectives;
    }

    public DataEnterer getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEnterer dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public Custodian getCustodian() {
        return custodian;
    }

    public void setCustodian(Custodian custodian) {
        this.custodian = custodian;
    }

    public LegalAuthenticator getLegalAuthenticator() {
        return legalAuthenticator;
    }

    public void setLegalAuthenticator(LegalAuthenticator legalAuthenticator) {
        this.legalAuthenticator = legalAuthenticator;
    }

    public Set<MpiMergedResidents> getSecondaryResidents() {
        return secondaryResidents;
    }

    public void setSecondaryResidents(Set<MpiMergedResidents> secondaryResidents) {
        this.secondaryResidents = secondaryResidents;
    }

    public Set<MpiMergedResidents> getMainResidents() {
        return mainResidents;
    }

    public void setMainResidents(Set<MpiMergedResidents> mainResidents) {
        this.mainResidents = mainResidents;
    }

    public Set<MPI> getMpi() {
        return mpi;
    }

    public void setMpi(Set<MPI> mpi) {
        this.mpi = mpi;
    }

    public List<ResidentDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<ResidentDevice> devices) {
        this.devices = devices;
    }

    public Set<Long> getUnmergedResidentIds() {
        return unmergedResidentIds;
    }

    public void setUnmergedResidentIds(Set<Long> unmergedResidentIds) {
        this.unmergedResidentIds = unmergedResidentIds;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }

    public String getPrevAddrStreet() {
        return prevAddrStreet;
    }

    public void setPrevAddrStreet(String prevAddrStreet) {
        this.prevAddrStreet = prevAddrStreet;
    }

    public String getPrevAddrCity() {
        return prevAddrCity;
    }

    public void setPrevAddrCity(String prevAddrCity) {
        this.prevAddrCity = prevAddrCity;
    }

    public String getPrevAddrState() {
        return prevAddrState;
    }

    public void setPrevAddrState(String prevAddrState) {
        this.prevAddrState = prevAddrState;
    }

    public String getPrevAddrZip() {
        return prevAddrZip;
    }

    public void setPrevAddrZip(String prevAddrZip) {
        this.prevAddrZip = prevAddrZip;
    }

    public String getHospitalPreference() {
        return hospitalPreference;
    }

    public void setHospitalPreference(String hospitalPreference) {
        this.hospitalPreference = hospitalPreference;
    }

    public String getTransportationPreference() {
        return transportationPreference;
    }

    public void setTransportationPreference(String transportationPreference) {
        this.transportationPreference = transportationPreference;
    }

    public String getAmbulancePreference() {
        return ambulancePreference;
    }

    public void setAmbulancePreference(String ambulancePreference) {
        this.ambulancePreference = ambulancePreference;
    }

    public String getPreadmissionNumber() {
        return preadmissionNumber;
    }

    public void setPreadmissionNumber(String preadmissionNumber) {
        this.preadmissionNumber = preadmissionNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getAuthorizationNumber() {
        return authorizationNumber;
    }

    public void setAuthorizationNumber(String authorizationNumber) {
        this.authorizationNumber = authorizationNumber;
    }

    public Date getAuthorizationNumberExpires() {
        return authorizationNumberExpires;
    }

    public void setAuthorizationNumberExpires(Date authorizationNumberExpires) {
        this.authorizationNumberExpires = authorizationNumberExpires;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getEvacuationStatus() {
        return evacuationStatus;
    }

    public void setEvacuationStatus(String evacuationStatus) {
        this.evacuationStatus = evacuationStatus;
    }

    public List<ResidentOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ResidentOrder> orders) {
        this.orders = orders;
    }

    public List<ResidentNotes> getAlertNotes() {
        return alertNotes;
    }

    public void setAlertNotes(List<ResidentNotes> alertNotes) {
        this.alertNotes = alertNotes;
    }

    public List<ResidentHealthPlan> getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(List<ResidentHealthPlan> healthPlans) {
        this.healthPlans = healthPlans;
    }

    public String getDentalInsurance() {
        return dentalInsurance;
    }

    public void setDentalInsurance(String dentalInsurance) {
        this.dentalInsurance = dentalInsurance;
    }

    public String getAdvanceDirectiveFreeText() {
        return advanceDirectiveFreeText;
    }

    public void setAdvanceDirectiveFreeText(String advanceDirectiveFreeText) {
        this.advanceDirectiveFreeText = advanceDirectiveFreeText;
    }

    public List<ResidentCareTeamMember> getResidentCareTeamMembers() {
        return residentCareTeamMembers;
    }

    public void setResidentCareTeamMembers(List<ResidentCareTeamMember> residentCareTeamMembers) {
        this.residentCareTeamMembers = residentCareTeamMembers;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getDischarged() {
        Date now = new Date();
        return (dischargeDate != null && dischargeDate.before(now)) || (BooleanUtils.isFalse(active));
    }

    public Boolean getInactive() {
        return BooleanUtils.isFalse(active);
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "preferred_name", length = 150)
    public String getPreferredName() {
        if (this.preferredName != null || person == null) {
            return this.preferredName;
        }
        String callMe = "";
        for (Name name : person.getNames()) {
            if ("L".equals(name.getNameUse())) {
                callMe = name.getPreferredName();
                break;
            }
        }
        return callMe;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "first_name", length = 150)
    public String getFirstName() {
        if (this.firstName != null || person == null) {
            return this.firstName;
        }
        String firstName = "";
        for (Name name : person.getNames()) {
            if ("L".equals(name.getNameUse())) {
                firstName = name.getGiven();
                break;
            }
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "last_name", length = 150)
    public String getLastName() {
        if (this.lastName != null || person == null) {
            return this.lastName;
        }
        String lastName = "";
        for (Name name : person.getNames()) {
            if ("L".equals(name.getNameUse())) {
                lastName = name.getFamily();
                break;
            }
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "middle_name", length = 150)
    public String getMiddleName() {
        if (this.middleName != null || person == null) {
            return this.middleName;
        }
        String middleName = "";
        for (Name name : person.getNames()) {
            if ("L".equals(name.getNameUse())) {
                middleName = name.getMiddle();
                break;
            }
        }
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFullName() {
        String result = StringUtils.isNotEmpty(getFirstName()) ? getFirstName() : "";
        if (StringUtils.isNotEmpty(getLastName())) {
            result = StringUtils.isNotEmpty(result) ? result + " " + getLastName() : getLastName();
        }
        return result;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(final List<Participant> participants) {
        this.participants = participants;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getCurrentPharmacyName() {
        return currentPharmacyName;
    }

    public void setCurrentPharmacyName(String currentPharmacyName) {
        this.currentPharmacyName = currentPharmacyName;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public InsurancePlan getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(InsurancePlan insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getInsurancePlanName() {
        return insurancePlanName;
    }

    public void setInsurancePlanName(String insurancePlanName) {
        this.insurancePlanName = insurancePlanName;
    }

    public String getConsanaXrefId() {
        return consanaXrefId;
    }

    public void setConsanaXrefId(String consanaXrefId) {
        this.consanaXrefId = consanaXrefId;
    }
}
