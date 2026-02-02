package com.scnsoft.eldermark.entity.history;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "resident_History")
public class ClientHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "resident_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private Client client;

    //updated on db level
    @Column(name = "last_updated", nullable = false, insertable = false, updatable = false)
    private Instant modifiedDate;

    //updated on db level
    @Column(name = "updated_datetime", insertable = false, updatable = false)
    private Instant updatedDatetime;

    //updated on db level
    @Column(name = "deleted_datetime", insertable = false, updatable = false)
    private Instant deletedDatetime;

    //UI editable fields
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender_id")
    private Long genderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", insertable = false, updatable = false)
    private CcdCode gender;

    @Column(name = "marital_status_id")
    private Long maritalStatus;

    @Column(name = "member_number")
    private String memberNumber;

    @Column(name = "retained")
    private Boolean retained;

    @Column(name = "primary_care_physician_first_name")
    private String primaryCarePhysicianFirstName;

    @Column(name = "primary_care_physician_last_name")
    private String primaryCarePhysicianLastName;

    @Column(name = "intake_date")
    private Instant intakeDate;

    @Column(name = "current_pharmacy_name")
    private String currentPharmacyName;

    @Column(name = "referral_source")
    private String referralSource;

    @Column(name = "medicaid_number")
    private String medicaidNumber;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "in_network_insurance_id")
    private Long inNetworkInsuranceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_network_insurance_id", insertable = false, updatable = false)
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "ssn")
    private String socialSecurity;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "medicare_number")
    private String medicareNumber;

    @Column(name = "insurance_plan")
    private String insurancePlan;

    @Column(name = "is_sharing")
    private Boolean isSharing;

    @Column(name = "risk_score")
    private String riskScore;

    @Column(name = "person_id")
    private Long personId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private Person person;

    //UI non-editable fields
    @Column(name = "facility_id")
    private Long communityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", insertable = false, updatable = false)
    private Community community;

    @Column(name = "ssn_last_four_digits", insertable = false, updatable = false)
    private String ssnLastFourDigits;

    @Column(name = "date_created")
    private Instant dateCreated;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "death_date")
    private Instant deathDate;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    @Column(name = "data_enterer_id")
    private Long dataEntererId;

    @Column(name = "ethnic_group_id")
    private Long ethnicGroupId;

    @Column(name = "provider_organization_id")
    private Long providerCommunityId;

    @Column(name = "legal_authenticator_id")
    private Long legalAuthenticatorId;

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "discharge_date")
    private Instant dischargeDate;

    @Column(name = "custodian_id")
    private Long custodianId;

    @Column(name = "medical_record_number")
    private String medicalRecordNumber;

    @Column(name = "prev_addr_street")
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

    @Column(name = "hospital_of_preference")
    private String hospitalPreference;

    @Column(name = "transportation_preference")
    private String transportationPreference;

    @Column(name = "ambulance_preference")
    private String ambulancePreference;

    @Column(name = "dental_Insurance")
    private String dentalInsurance;

    @Column(name = "evacuation_status")
    private String evacuationStatus;

    @Column(name = "religion_id")
    private Long religionId;

    @Column(name = "race_id")
    private Long raceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", insertable = false, updatable = false)
    private CcdCode race;

    @Column(name = "legacy_id")
    private String legacyId;

    @Column(name = "database_id")
    private Long organizationId;

    @Column(name = "legacy_table")
    private String legacyTable;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_type")
    private HieConsentPolicyType hieConsentPolicyType;

    @Column(name = "last_updated", insertable = false, updatable = false)
    private Instant lastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Instant getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Instant updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Long maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
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

    public String getCurrentPharmacyName() {
        return currentPharmacyName;
    }

    public void setCurrentPharmacyName(String currentPharmacyName) {
        this.currentPharmacyName = currentPharmacyName;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public Long getInNetworkInsuranceId() {
        return inNetworkInsuranceId;
    }

    public void setInNetworkInsuranceId(Long inNetworkInsurance) {
        this.inNetworkInsuranceId = inNetworkInsurance;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
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

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public Boolean getSharing() {
        return isSharing;
    }

    public void setSharing(Boolean sharing) {
        isSharing = sharing;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Instant getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Instant deathDate) {
        this.deathDate = deathDate;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getDataEntererId() {
        return dataEntererId;
    }

    public void setDataEntererId(Long dataEntererId) {
        this.dataEntererId = dataEntererId;
    }

    public Long getEthnicGroupId() {
        return ethnicGroupId;
    }

    public void setEthnicGroupId(Long ethnicGroupId) {
        this.ethnicGroupId = ethnicGroupId;
    }

    public Long getProviderCommunityId() {
        return providerCommunityId;
    }

    public void setProviderCommunityId(Long providerCommunityId) {
        this.providerCommunityId = providerCommunityId;
    }

    public Long getLegalAuthenticatorId() {
        return legalAuthenticatorId;
    }

    public void setLegalAuthenticatorId(Long legalAuthenticatorId) {
        this.legalAuthenticatorId = legalAuthenticatorId;
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

    public Long getCustodianId() {
        return custodianId;
    }

    public void setCustodianId(Long custodianId) {
        this.custodianId = custodianId;
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

    public String getDentalInsurance() {
        return dentalInsurance;
    }

    public void setDentalInsurance(String dentalInsurance) {
        this.dentalInsurance = dentalInsurance;
    }

    public String getEvacuationStatus() {
        return evacuationStatus;
    }

    public void setEvacuationStatus(String evacuationStatus) {
        this.evacuationStatus = evacuationStatus;
    }

    public Long getReligionId() {
        return religionId;
    }

    public void setReligionId(Long religionId) {
        this.religionId = religionId;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
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

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(final Long genderId) {
        this.genderId = genderId;
    }

    public void setGender(final CcdCode gender) {
        this.gender = gender;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(final CcdCode race) {
        this.race = race;
    }

    public CcdCode getGender() {
        return gender;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(final InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(final Community community) {
        this.community = community;
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

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public HieConsentPolicyType getHieConsentPolicyType() {
        return hieConsentPolicyType;
    }

    public void setHieConsentPolicyType(HieConsentPolicyType hieConsentPolicyType) {
        this.hieConsentPolicyType = hieConsentPolicyType;
    }
}
