package com.scnsoft.eldermark.dto.prospect;

import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityFieldsAware;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.entity.prospect.Veteran;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ProspectDto implements ProspectSecurityFieldsAware {
    @NotNull(groups = ValidationGroups.Update.class)
    private Long id;

    private Boolean isActive;

    @NotNull
    private Long organizationId;
    private String organizationTitle;
    @NotEmpty
    @Size(max = 256)
    private String firstName;
    @NotEmpty
    @Size(max = 256)
    private String lastName;
    @Size(max = 256)
    private String middleName;
    private String fullName;
    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String cellPhone;
    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;
    @Size(max = 256)
    private String insurancePaymentPlan;
    private Long insuranceNetworkId;
    @NotNull
    private Long genderId;
    private String gender;

    @Pattern(regexp = ValidationRegExpConstants.SSN_REGEXP)
    private String ssn;
    private Long maritalStatusId;
    private String maritalStatus;
    private Long raceId;
    private String race;
    @NotNull
    private String birthDate;
    @NotNull
    private Long communityId;
    private String communityTitle;
    private Long moveInDate;
    private Long rentalAgreementSignedDate;
    private Long assessmentDate;
    @Size(max = 256)
    private String referralSource;
    @Size(max = 256)
    private String notes;
    @Valid
    private RelatedPartyDto relatedParty;
    @Valid
    private AddressDto address;
    private SecondOccupantDto secondOccupant;
    private PrimaryContactDto primaryContact;

    private MultipartFile avatar;
    private Long avatarId;
    private String avatarName;
    private Boolean shouldRemoveAvatar;

    private Veteran veteranStatusName;
    private String veteranStatusTitle;

    private boolean canEdit;
    private boolean canEditSsn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getInsuranceNetworkId() {
        return insuranceNetworkId;
    }

    public void setInsuranceNetworkId(Long insuranceNetworkId) {
        this.insuranceNetworkId = insuranceNetworkId;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Long getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Long maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(Long moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Long getRentalAgreementSignedDate() {
        return rentalAgreementSignedDate;
    }

    public void setRentalAgreementSignedDate(Long rentalAgreementSignedDate) {
        this.rentalAgreementSignedDate = rentalAgreementSignedDate;
    }

    public Long getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Long assessmentDate) {
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

    public RelatedPartyDto getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(RelatedPartyDto relatedParty) {
        this.relatedParty = relatedParty;
    }

    public SecondOccupantDto getSecondOccupant() {
        return secondOccupant;
    }

    public void setSecondOccupant(SecondOccupantDto secondOccupant) {
        this.secondOccupant = secondOccupant;
    }

    public PrimaryContactDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(PrimaryContactDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getInsurancePaymentPlan() {
        return insurancePaymentPlan;
    }

    public void setInsurancePaymentPlan(String insurancePaymentPlan) {
        this.insurancePaymentPlan = insurancePaymentPlan;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
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

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(Boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }

    public Veteran getVeteranStatusName() {
        return veteranStatusName;
    }

    public void setVeteranStatusName(Veteran veteranStatusName) {
        this.veteranStatusName = veteranStatusName;
    }

    public String getVeteranStatusTitle() {
        return veteranStatusTitle;
    }

    public void setVeteranStatusTitle(String veteranStatusTitle) {
        this.veteranStatusTitle = veteranStatusTitle;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanEditSsn() {
        return canEditSsn;
    }

    public void setCanEditSsn(boolean canEditSsn) {
        this.canEditSsn = canEditSsn;
    }

    public String getOrganizationTitle() {
        return organizationTitle;
    }

    public void setOrganizationTitle(String organizationTitle) {
        this.organizationTitle = organizationTitle;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }
}
