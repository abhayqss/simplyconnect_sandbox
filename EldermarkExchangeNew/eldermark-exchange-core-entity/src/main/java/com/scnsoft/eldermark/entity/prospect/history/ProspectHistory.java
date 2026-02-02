package com.scnsoft.eldermark.entity.prospect.history;

import com.scnsoft.eldermark.beans.ProspectDeactivationReason;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import com.scnsoft.eldermark.entity.prospect.RelatedPartyRelationship;
import com.scnsoft.eldermark.entity.prospect.Veteran;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "ProspectHistory")
public class ProspectHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prospect_id", nullable = false)
    private Prospect prospect;

    @Column(name = "prospect_id", nullable = false, insertable = false, updatable = false)
    private Long prospectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false)
    private Organization organization;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Long organizationId;

    @Column(name = "external_id")
    private Long externalId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "insurance_plan")
    private String insurancePlan;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @Column(name = "ssn")
    private String socialSecurity;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private CcdCode race;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "veteran")
    private Veteran veteran;

    @Column(name = "cell_phone")
    private String cellPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Community community;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long communityId;

    @Column(name = "move_in_date")
    private Instant moveInDate;

    @Column(name = "rental_agreement_date")
    private Instant rentalAgreementDate;

    @Column(name = "assessment_date")
    private Instant assessmentDate;

    @Column(name = "referral_source")
    private String referralSource;

    @Column(name = "notes")
    private String notes;

    @Column(name = "related_party_first_name")
    private String relatedPartyFirstName;

    @Column(name = "related_party_last_name")
    private String relatedPartyLastName;

    @Column(name = "related_party_cell_phone")
    private String relatedPartyCellPhone;

    @Column(name = "related_party_email")
    private String relatedPartyEmail;

    @Column(name = "related_party_city")
    private String relatedPartyCity;

    @Column(name = "related_party_street")
    private String relatedPartyStreet;

    @Column(name = "related_party_state")
    private String relatedPartyState;

    @Column(name = "related_party_zip")
    private String relatedPartyZip;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_party_relationship")
    private RelatedPartyRelationship relatedPartyRelationship;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "second_occupant_history_id")
    private SecondOccupantHistory secondOccupantHistory;

    @Column(name = "deactivation_date")
    private Instant deactivationDate;

    @Column(name = "activation_date")
    private Instant activationDate;

    @Column(name = "activation_comment")
    private String activationComment;

    @Column(name = "deactivation_comment")
    private String deactivationComment;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "deactivation_reason")
    private ProspectDeactivationReason deactivationReason;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Employee updatedBy;

    @Column(name = "updated_by_id", insertable = false, updatable = false)
    private Long updatedById;

    @Column(name = "created_date")
    private Instant createdDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Employee createdBy;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prospect getProspect() {
        return prospect;
    }

    public void setProspect(Prospect prospect) {
        this.prospect = prospect;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
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

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Veteran getVeteran() {
        return veteran;
    }

    public void setVeteran(Veteran veteran) {
        this.veteran = veteran;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String stateId) {
        this.state = stateId;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

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

    public Instant getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(Instant moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Instant getRentalAgreementDate() {
        return rentalAgreementDate;
    }

    public void setRentalAgreementDate(Instant rentalAgreementDate) {
        this.rentalAgreementDate = rentalAgreementDate;
    }

    public Instant getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Instant assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRelatedPartyFirstName() {
        return relatedPartyFirstName;
    }

    public void setRelatedPartyFirstName(String relatedPartyFirstName) {
        this.relatedPartyFirstName = relatedPartyFirstName;
    }

    public String getRelatedPartyLastName() {
        return relatedPartyLastName;
    }

    public void setRelatedPartyLastName(String relatedPartyLastName) {
        this.relatedPartyLastName = relatedPartyLastName;
    }

    public String getRelatedPartyCellPhone() {
        return relatedPartyCellPhone;
    }

    public void setRelatedPartyCellPhone(String relatedPartyCellPhone) {
        this.relatedPartyCellPhone = relatedPartyCellPhone;
    }

    public String getRelatedPartyEmail() {
        return relatedPartyEmail;
    }

    public void setRelatedPartyEmail(String relatedPartyEmail) {
        this.relatedPartyEmail = relatedPartyEmail;
    }

    public String getRelatedPartyCity() {
        return relatedPartyCity;
    }

    public void setRelatedPartyCity(String relatedPartyCity) {
        this.relatedPartyCity = relatedPartyCity;
    }

    public String getRelatedPartyStreet() {
        return relatedPartyStreet;
    }

    public void setRelatedPartyStreet(String relatedPartyStreet) {
        this.relatedPartyStreet = relatedPartyStreet;
    }

    public String getRelatedPartyState() {
        return relatedPartyState;
    }

    public void setRelatedPartyState(String relatedPartyStateId) {
        this.relatedPartyState = relatedPartyStateId;
    }

    public String getRelatedPartyZip() {
        return relatedPartyZip;
    }

    public void setRelatedPartyZip(String relatedPartyZip) {
        this.relatedPartyZip = relatedPartyZip;
    }

    public RelatedPartyRelationship getRelatedPartyRelationship() {
        return relatedPartyRelationship;
    }

    public void setRelatedPartyRelationship(RelatedPartyRelationship relatedPartyRelationship) {
        this.relatedPartyRelationship = relatedPartyRelationship;
    }

    public SecondOccupantHistory getSecondOccupantHistory() {
        return secondOccupantHistory;
    }

    public void setSecondOccupantHistory(SecondOccupantHistory secondOccupantHistory) {
        this.secondOccupantHistory = secondOccupantHistory;
    }

    public Instant getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Instant deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Instant activationDate) {
        this.activationDate = activationDate;
    }

    public String getActivationComment() {
        return activationComment;
    }

    public void setActivationComment(String activationComment) {
        this.activationComment = activationComment;
    }

    public String getDeactivationComment() {
        return deactivationComment;
    }

    public void setDeactivationComment(String deactivationComment) {
        this.deactivationComment = deactivationComment;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ProspectDeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(ProspectDeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Employee getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Employee updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
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
}
