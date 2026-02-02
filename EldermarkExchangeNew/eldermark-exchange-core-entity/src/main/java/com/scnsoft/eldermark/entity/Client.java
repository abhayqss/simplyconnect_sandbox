package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.projection.AssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.beans.projection.IdActiveCreatedLastUpdatedAware;
import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;
import com.scnsoft.eldermark.entity.client.ClientNotes;
import com.scnsoft.eldermark.entity.client.ClientOrder;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContact;
import com.scnsoft.eldermark.entity.client.insurance.ClientInsuranceAuthorization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.document.facesheet.Language;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "resident")
@Access(AccessType.FIELD)
public class Client extends StringLegacyTableAwareEntity implements Serializable,
        ClientSecurityAwareEntity, IdActiveCreatedLastUpdatedAware, IdOrganizationIdActiveAware, EntityWithAvatar {

    private static final long serialVersionUID = 1L;

    public Client() {
    }

    public Client(Long id) {
        setId(id);
    }

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Community community;

    @Column(name = "facility_id", insertable = false, updatable = false)
    private Long communityId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @Column(name = "retained")
    private Boolean retained;

    @Column(name = "primary_care_physician_first_name")
    private String primaryCarePhysicianFirstName;

    @Column(name = "primary_care_physician_last_name")
    private String primaryCarePhysicianLastName;

    @Column(name = "intake_date")
    private Instant intakeDate;

    @Column(name = "referral_source")
    private String referralSource;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "is_sharing")
    private Boolean isSharing;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<Event> events;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "ssn")
    private String socialSecurity;

    @Column(name = "ssn_last_four_digits", insertable = false, updatable = false)
    private String ssnLastFourDigits;

    @Column(name = "date_created")
    private Instant createdDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "maiden_name")
    private String maidenName;

    @Column(name = "death_date")
    private Instant deathDate;

    @Column(name = "genacross_id")
    private Long genacrossId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data_enterer_id")
    private DataEnterer dataEnterer;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<AdvanceDirective> advanceDirectives;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private BirthplaceAddress birthplaceAddress;

    @ManyToOne
    @JoinColumn(name = "ethnic_group_id")
    private CcdCode ethnicGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_organization_id")
    private Community providerOrganization;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Guardian> guardians;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Language> languages;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "legal_authenticator_id")
    private LegalAuthenticator legalAuthenticator;

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<DocumentationOf> documentationOfs;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "custodian_id")
    private Custodian custodian;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "client")
    private List<Author> authors;

    @Transient
    private String preferredName;

    @Column(name = "medical_record_number")
    private String medicalRecordNumber;

    @Column(name = "prev_addr_street") // PersonAddress
    private String prevAddrStreet;

    @Column(name = "prev_addr_city")
    private String prevAddrCity;

    @Column(name = "prev_addr_state")
    private String prevAddrState;

    @Column(name = "prev_addr_zip")
    private String prevAddrZip;

    @Column(name = "veteran")
    private String veteran;

    @Column(name = "unit_number")
    private String unitNumber;

    @Column(name = "current_pharmacy_name")
    private String currentPharmacyName;

    @Column(name = "hospital_of_preference")
    private String hospitalPreference;

    @Column(name = "transportation_preference")
    private String transportationPreference;

    @Column(name = "ambulance_preference")
    private String ambulancePreference;

    @Column(name = "medicare_number")
    private String medicareNumber;

    @Column(name = "medicaid_number")
    private String medicaidNumber;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "member_number")
    private String memberNumber;

    @Column(name = "dental_Insurance")
    private String dentalInsurance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<ClientNotes> alertNotes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<ClientOrder> orders;

    @Column(name = "advance_directive_free_text", columnDefinition = "text")
    private String advanceDirectiveFreeText;

    @Column(name = "evacuation_status")
    private String evacuationStatus;

    @ManyToOne
    @JoinColumn(name = "religion_id")
    private CcdCode religion;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private CcdCode race;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<ClientHealthPlan> healthPlans;

    @Transient
    private MultipartFile multipartFile;

    @Transient
    private Boolean shouldRemoveAvatar;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "avatar_id", insertable = false, updatable = false)
    private Long avatarId;

    @Column(name = "risk_score")
    private String riskScore;

    @Column(name = "insurance_plan")
    private String insurancePlan;

    @Column(name = "last_updated", insertable = false, updatable = false)
    private Instant lastUpdated;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = ClientComprehensiveAssessment.class, mappedBy = "client")
    private List<ClientComprehensiveAssessment> clientComprehensiveAssessments;

    @Column(name = "citizenship")
    private String citizenship;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Employee createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "employee_associated_residents", joinColumns = {@JoinColumn(name = "resident_id")}, inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private Employee associatedEmployee;

    @ElementCollection
    @CollectionTable(name = "employee_associated_residents", joinColumns = @JoinColumn(name = "resident_id"))
    @Column(name = "employee_id", insertable = false, updatable = false)
    private List<Long> associatedEmployeeIds;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    @Column(name = "opt_out")
    private Boolean isOptOut;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<AdmittanceHistory> admittanceHistories;

    @Column(name = "consana_xref_id")
    private String consanaXrefId;

    @ManyToMany
    @JoinTable(name = "Employee_FavouriteResident",
            joinColumns = @JoinColumn(name = "favourite_resident_id",
                    nullable = false,
                    insertable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(name = "employee_id",
                    nullable = false,
                    insertable = false,
                    updatable = false))
    private List<Employee> addedAsFavouriteToEmployees;

    @ElementCollection
    @CollectionTable(name = "Employee_FavouriteResident",
            joinColumns = @JoinColumn(name = "favourite_resident_id", nullable = false))
    @Column(name = "employee_id", nullable = false)
    private Set<Long> addedAsFavouriteToEmployeeIds;

    @Column(name = "hp_member_identifier")
    private String healthPartnersMemberIdentifier;

    @Column(name = "exit_date")
    private Instant exitDate;

    @Column(name = "deactivation_date")
    private Instant deactivationDate;

    @Column(name = "activation_date")
    private Instant activationDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "exit_comment")
    private String exitComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivation_reason")
    private ClientDeactivationReason deactivationReason;

    @Column(name = "program_type")
    private String programType;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "birth_order")
    private Integer birthOrder;

    @Column(name = "death_indicator")
    private Boolean deathIndicator;

    @Column(name = "pcc_patient_id")
    private Long pccPatientId;

    @Column(name = "outpatient")
    private Boolean outpatient;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_account_number")
    private MPI patientAccountNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mother_account_number")
    private MPI mothersId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mother_person_id")
    private Person mother;

    @Column(name = "pharmacy_pid")
    private String pharmacyPid;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "primary_contact_id")
    private ClientPrimaryContact primaryContact;

    @Column(name = "primary_contact_id", insertable = false, updatable = false)
    private Long primaryContactId;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientInsuranceAuthorization> insuranceAuthorizations;

    @Column(name = "hie_consent_policy_obtained_from")
    private String hieConsentPolicyObtainedFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_obtained_by")
    private HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_source")
    private HieConsentPolicySource hieConsentPolicySource;

    @Column(name = "hie_consent_policy_update_datetime")
    private Instant hieConsentPolicyUpdateDateTime;

    @Column(name = "hie_consent_policy_updated_by_employee_id")
    private Long hieConsentPolicyUpdatedByEmployeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_type", nullable = false)
    private HieConsentPolicyType hieConsentPolicyType;

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    public String getPrimaryCarePhysicianFirstName() {
        return primaryCarePhysicianFirstName;
    }

    public void setPrimaryCarePhysicianFirstName(String primaryCarePhysicianFirstName) {
        this.primaryCarePhysicianFirstName = primaryCarePhysicianFirstName;
    }

    public String getPrimaryCarePhysicianLastName() {
        return primaryCarePhysicianLastName;
    }

    public void setPrimaryCarePhysicianLastName(String primaryCarePhysicianLastName) {
        this.primaryCarePhysicianLastName = primaryCarePhysicianLastName;
    }

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public Instant getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Instant deathDate) {
        this.deathDate = deathDate;
    }

    public DataEnterer getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEnterer dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public List<AdvanceDirective> getAdvanceDirectives() {
        return advanceDirectives;
    }

    public void setAdvanceDirectives(List<AdvanceDirective> advanceDirectives) {
        this.advanceDirectives = advanceDirectives;
    }

    public BirthplaceAddress getBirthplaceAddress() {
        return birthplaceAddress;
    }

    public void setBirthplaceAddress(BirthplaceAddress birthplaceAddress) {
        this.birthplaceAddress = birthplaceAddress;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public CcdCode getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(CcdCode ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public Community getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(Community providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public List<Guardian> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<Guardian> guardians) {
        this.guardians = guardians;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public LegalAuthenticator getLegalAuthenticator() {
        return legalAuthenticator;
    }

    public void setLegalAuthenticator(LegalAuthenticator legalAuthenticator) {
        this.legalAuthenticator = legalAuthenticator;
    }

    public Instant getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Instant admitDate) {
        this.admitDate = admitDate;
    }

    public Instant getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Instant dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public List<DocumentationOf> getDocumentationOfs() {
        return documentationOfs;
    }

    public void setDocumentationOfs(List<DocumentationOf> documentationOfs) {
        this.documentationOfs = documentationOfs;
    }

    public Custodian getCustodian() {
        return custodian;
    }

    public void setCustodian(Custodian custodian) {
        this.custodian = custodian;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "preferred_name")
    public String getPreferredName() {
        if (this.preferredName != null || person == null) {
            return this.preferredName;
        }
        return person.getNames()
                .stream()
                .filter(name -> "L".equals(name.getNameUse()))
                .findFirst().map(Name::getPreferredName)
                .orElse("");
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
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

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getCurrentPharmacyName() {
        return currentPharmacyName;
    }

    public void setCurrentPharmacyName(String currentPharmacyName) {
        this.currentPharmacyName = currentPharmacyName;
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

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
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

    public String getDentalInsurance() {
        return dentalInsurance;
    }

    public void setDentalInsurance(String dentalInsurance) {
        this.dentalInsurance = dentalInsurance;
    }

    public List<ClientNotes> getAlertNotes() {
        return alertNotes;
    }

    public void setAlertNotes(List<ClientNotes> alertNotes) {
        this.alertNotes = alertNotes;
    }

    public List<ClientOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ClientOrder> orders) {
        this.orders = orders;
    }

    public String getAdvanceDirectiveFreeText() {
        return advanceDirectiveFreeText;
    }

    public void setAdvanceDirectiveFreeText(String advanceDirectiveFreeText) {
        this.advanceDirectiveFreeText = advanceDirectiveFreeText;
    }

    public String getEvacuationStatus() {
        return evacuationStatus;
    }

    public void setEvacuationStatus(String evacuationStatus) {
        this.evacuationStatus = evacuationStatus;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public CcdCode getReligion() {
        return religion;
    }

    public void setReligion(CcdCode religion) {
        this.religion = religion;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public List<ClientHealthPlan> getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(List<ClientHealthPlan> healthPlans) {
        this.healthPlans = healthPlans;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public Long getAvatarId() {
        return avatarId;
    }

    @Override
    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(Boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public String getFullName() {
        return Stream.of(getFirstName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getGenacrossId() {
        return genacrossId;
    }

    public void setGenacrossId(Long genacrossId) {
        this.genacrossId = genacrossId;
    }

    public Boolean getSharing() {
        return isSharing;
    }

    public void setSharing(Boolean sharing) {
        isSharing = sharing;
    }

    public List<ClientComprehensiveAssessment> getClientComprehensiveAssessments() {
        return clientComprehensiveAssessments;
    }

    public void setClientComprehensiveAssessments(List<ClientComprehensiveAssessment> clientComprehensiveAssessments) {
        this.clientComprehensiveAssessments = clientComprehensiveAssessments;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Boolean getOptOut() {
        return isOptOut;
    }

    public void setOptOut(Boolean optOut) {
        this.isOptOut = optOut;
    }

    public List<AdmittanceHistory> getAdmittanceHistories() {
        return admittanceHistories;
    }

    public void setAdmittanceHistories(List<AdmittanceHistory> admittanceHistories) {
        this.admittanceHistories = admittanceHistories;
    }

    public Employee getAssociatedEmployee() {
        return associatedEmployee;
    }

    public void setAssociatedEmployee(Employee associatedEmployee) {
        this.associatedEmployee = associatedEmployee;
    }

    public List<Long> getAssociatedEmployeeIds() {
        return associatedEmployeeIds;
    }

    public void setAssociatedEmployeeIds(List<Long> associatedEmployeeIds) {
        this.associatedEmployeeIds = associatedEmployeeIds;
    }

    public String getConsanaXrefId() {
        return consanaXrefId;
    }

    public void setConsanaXrefId(String consanaXrefId) {
        this.consanaXrefId = consanaXrefId;
    }

    public List<Employee> getAddedAsFavouriteToEmployees() {
        return addedAsFavouriteToEmployees;
    }

    public void setAddedAsFavouriteToEmployees(List<Employee> addedAsFavouriteToEmployees) {
        this.addedAsFavouriteToEmployees = addedAsFavouriteToEmployees;
    }

    public Set<Long> getAddedAsFavouriteToEmployeeIds() {
        return addedAsFavouriteToEmployeeIds;
    }

    public void setAddedAsFavouriteToEmployeeIds(Set<Long> addedAsFavouriteToEmployeeIds) {
        this.addedAsFavouriteToEmployeeIds = addedAsFavouriteToEmployeeIds;
    }

    public String getHealthPartnersMemberIdentifier() {
        return healthPartnersMemberIdentifier;
    }

    public void setHealthPartnersMemberIdentifier(String healthPartnersMemberIdentifier) {
        this.healthPartnersMemberIdentifier = healthPartnersMemberIdentifier;
    }

    public Instant getExitDate() {
        return exitDate;
    }

    public void setExitDate(final Instant exitDate) {
        this.exitDate = exitDate;
    }

    public Instant getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(final Instant deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(final Instant activationDate) {
        this.activationDate = activationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getExitComment() {
        return exitComment;
    }

    public void setExitComment(final String exitComment) {
        this.exitComment = exitComment;
    }

    public ClientDeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(final ClientDeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(final String programType) {
        this.programType = programType;
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

    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }

    public Long getPccPatientId() {
        return pccPatientId;
    }

    public void setPccPatientId(Long pccPatientId) {
        this.pccPatientId = pccPatientId;
    }

    public Boolean getOutpatient() {
        return outpatient;
    }

    public void setOutpatient(Boolean outpatient) {
        this.outpatient = outpatient;
    }

    public MPI getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(MPI patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public MPI getMothersId() {
        return mothersId;
    }

    public void setMothersId(MPI mothersId) {
        this.mothersId = mothersId;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public String getPharmacyPid() {
        return pharmacyPid;
    }

    public void setPharmacyPid(String pharmacyPid) {
        this.pharmacyPid = pharmacyPid;
    }

    public ClientPrimaryContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ClientPrimaryContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public Long getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Long primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    public List<ClientInsuranceAuthorization> getInsuranceAuthorizations() {
        return insuranceAuthorizations;
    }

    public void setInsuranceAuthorizations(List<ClientInsuranceAuthorization> insuranceAuthorizations) {
        this.insuranceAuthorizations = insuranceAuthorizations;
    }

    public String getHieConsentPolicyObtainedFrom() {
        return hieConsentPolicyObtainedFrom;
    }

    public void setHieConsentPolicyObtainedFrom(String hieConsentPolicyObtainedFrom) {
        this.hieConsentPolicyObtainedFrom = hieConsentPolicyObtainedFrom;
    }

    public HieConsentPolicyObtainedBy getHieConsentPolicyObtainedBy() {
        return hieConsentPolicyObtainedBy;
    }

    public void setHieConsentPolicyObtainedBy(HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy) {
        this.hieConsentPolicyObtainedBy = hieConsentPolicyObtainedBy;
    }

    public HieConsentPolicySource getHieConsentPolicySource() {
        return hieConsentPolicySource;
    }

    public void setHieConsentPolicySource(HieConsentPolicySource hieConsentPolicySource) {
        this.hieConsentPolicySource = hieConsentPolicySource;
    }

    public Instant getHieConsentPolicyUpdateDateTime() {
        return hieConsentPolicyUpdateDateTime;
    }

    public void setHieConsentPolicyUpdateDateTime(Instant hieConsentPolicyUpdateDateTime) {
        this.hieConsentPolicyUpdateDateTime = hieConsentPolicyUpdateDateTime;
    }

    public Long getHieConsentPolicyUpdatedByEmployeeId() {
        return hieConsentPolicyUpdatedByEmployeeId;
    }

    public void setHieConsentPolicyUpdatedByEmployeeId(Long hieConsentPolicyUpdatedByEmployeeId) {
        this.hieConsentPolicyUpdatedByEmployeeId = hieConsentPolicyUpdatedByEmployeeId;
    }

    public HieConsentPolicyType getHieConsentPolicyType() {
        return hieConsentPolicyType;
    }

    public void setHieConsentPolicyType(HieConsentPolicyType hieConsentPolicyType) {
        this.hieConsentPolicyType = hieConsentPolicyType;
    }
}
