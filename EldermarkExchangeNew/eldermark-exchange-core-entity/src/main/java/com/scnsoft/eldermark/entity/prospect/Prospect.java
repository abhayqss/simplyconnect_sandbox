package com.scnsoft.eldermark.entity.prospect;

import com.scnsoft.eldermark.beans.ProspectDeactivationReason;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "Prospect")
public class Prospect extends BasicEntity implements EntityWithAvatar {

    @Column(name = "external_id")
    private Long externalId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @ManyToOne
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "related_party_person_id")
    private Person relatedPartyPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_party_relationship")
    private RelatedPartyRelationship relatedPartyRelationship;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "second_occupant_id")
    private SecondOccupant secondOccupant;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "primary_contact_id")
    private ProspectPrimaryContact primaryContact;

    @Column(name = "primary_contact_id", insertable = false, updatable = false)
    private Long primaryContactId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "avatar_id", insertable = false, updatable = false)
    private Long avatarId;

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

    @Column(name = "last_modified_date", insertable = false, updatable = false)
    private Instant lastModifiedDate;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Instant createdDate;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Employee createdBy;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Employee updatedBy;

    @Column(name = "updated_by_id", insertable = false, updatable = false)
    private Long updatedById;

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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

    public Person getRelatedPartyPerson() {
        return relatedPartyPerson;
    }

    public void setRelatedPartyPerson(Person relatedPartyPerson) {
        this.relatedPartyPerson = relatedPartyPerson;
    }

    public RelatedPartyRelationship getRelatedPartyRelationship() {
        return relatedPartyRelationship;
    }

    public void setRelatedPartyRelationship(RelatedPartyRelationship relatedPartyRelationship) {
        this.relatedPartyRelationship = relatedPartyRelationship;
    }

    public SecondOccupant getSecondOccupant() {
        return secondOccupant;
    }

    public void setSecondOccupant(SecondOccupant secondOccupant) {
        this.secondOccupant = secondOccupant;
    }

    public ProspectPrimaryContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ProspectPrimaryContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public Long getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Long primaryContactId) {
        this.primaryContactId = primaryContactId;
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

    public void setLastModifiedDate(Instant lastUpdated) {
        this.lastModifiedDate = lastUpdated;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant creationDate) {
        this.createdDate = creationDate;
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
}
