package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAware;
import com.scnsoft.eldermark.dto.client.PrimaryContactDto;
import com.scnsoft.eldermark.dto.client.insurance.InsuranceAuthorizationDto;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class ClientDto extends ClientEssentialsDto implements ClientSecurityFieldsAware {

    private String middleName; //todo is it used at all?

    private String fullName;

    @Pattern(regexp = ValidationRegExpConstants.SSN_REGEXP)
    private String ssn;

    @NotNull
    private Long genderId;
    private String gender;

    private Long raceId;
    private String race;

    private Long maritalStatusId;
    private String maritalStatus;


    @NotNull
    private @Valid
    AddressDto address;

    @NotNull
    private Long organizationId;
    private String organization;
    private String organizationPhone;


    @NotNull
    private Long communityId;
    private String community;
    private String communityPhone;

    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;   //todo rename to homePhone?

    @NotEmpty
    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String cellPhone;

    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;
    private Long insuranceNetworkId;

    @Size(max = 256)
    private String insurancePaymentPlan;

    private List<@Valid InsuranceAuthorizationDto> insuranceAuthorizations;

    @Size(max = 256)
    private String groupNumber;

    @Size(max = 256)
    private String memberNumber;

    @Size(max = 256)
    private String medicareNumber;

    @Size(max = 256)
    private String medicaidNumber;

    private Boolean retained;

    @Size(max = 256)
    private String primaryCarePhysicianFirstName;
    @Size(max = 256)
    private String primaryCarePhysicianLastName;

    private Long intakeDate;

    @Size(max = 256)
    private String referralSource;

    @Size(max = 256)
    private String currentPharmacyName;
    private Boolean isActive;
    private String ssnLastFourDigits;
    private String telecoms;        //todo what is the usage?
    private List<String> aliases;
    private List<String> identifier;
    private String primaryLanguage;
    private String patientAccountNumber;
    private List<String> ethnicGroup;
    private String nationality;
    private String religion;
    private List<String> citizenship;
    private String veteransMilitaryStatus;
    private Long deathDateTime;
    private Boolean isDataShareEnabled;

    private MultipartFile avatar;
    private Long avatarId;
    private String avatarName;
    private Boolean shouldRemoveAvatar;

    @Size(max = 256)
    private String riskScore;

    private List<String> pharmacies;

    private boolean canEdit;
    private boolean canEditSsn;
    private Boolean editable = false; //todo 2 'editable' flags
    private boolean canRequestRide;
    private boolean canViewRideHistory;

    private Set<Long> admitDates;
    private Set<Long> dischargeDates;

    private ClientAssociatedContactDto associatedContact;

    @Size(max = 128)
    private String unit;

    private Long createdDate;
    private Long deactivatedDate;

    private boolean manuallyCreated;

    private String pharmacyPid;

    private PrimaryContactDto primaryContact;

    private HieConsentPolicyType hieConsentPolicyName;
    private String hieConsentPolicyTitle;
    private HieConsentPolicyObtainedBy hieConsentPolicyObtainedBy;
    private Long hieConsentPolicyObtainedDate;
    private String hieConsentPolicyObtainedFrom;
    private String pointClickCareMedicalRecordNumber;

    public ClientDto() {
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

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public Long getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Long maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Long getInsuranceNetworkId() {
        return insuranceNetworkId;
    }

    public void setInsuranceNetworkId(Long insuranceNetworkId) {
        this.insuranceNetworkId = insuranceNetworkId;
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

    public Long getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Long intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getCurrentPharmacyName() {
        return currentPharmacyName;
    }

    public void setCurrentPharmacyName(String currentPharmacyName) {
        this.currentPharmacyName = currentPharmacyName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public String getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(String telecoms) {
        this.telecoms = telecoms;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<String> identifier) {
        this.identifier = identifier;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(String patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public List<String> getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(List<String> ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public List<String> getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(List<String> citizenship) {
        this.citizenship = citizenship;
    }

    public String getVeteransMilitaryStatus() {
        return veteransMilitaryStatus;
    }

    public void setVeteransMilitaryStatus(String veteransMilitaryStatus) {
        this.veteransMilitaryStatus = veteransMilitaryStatus;
    }

    public Long getDeathDateTime() {
        return deathDateTime;
    }

    public void setDeathDateTime(Long deathDateTime) {
        this.deathDateTime = deathDateTime;
    }

    public Boolean getIsDataShareEnabled() {
        return isDataShareEnabled;
    }

    public void setIsDataShareEnabled(Boolean dataShareEnabled) {
        isDataShareEnabled = dataShareEnabled;
    }

    public String getInsurancePaymentPlan() {
        return insurancePaymentPlan;
    }

    public void setInsurancePaymentPlan(String insurancePaymentPlan) {
        this.insurancePaymentPlan = insurancePaymentPlan;
    }

    public List<InsuranceAuthorizationDto> getInsuranceAuthorizations() {
        return insuranceAuthorizations;
    }

    public void setInsuranceAuthorizations(List<InsuranceAuthorizationDto> insuranceAuthorizations) {
        this.insuranceAuthorizations = insuranceAuthorizations;
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

    public List<String> getPharmacies() {
        return pharmacies;
    }

    public void setPharmacies(List<String> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Set<Long> getAdmitDates() {
        return admitDates;
    }

    public void setAdmitDates(Set<Long> admitDates) {
        this.admitDates = admitDates;
    }

    public Set<Long> getDischargeDates() {
        return dischargeDates;
    }

    public void setDischargeDates(Set<Long> dischargeDates) {
        this.dischargeDates = dischargeDates;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }

    public String getCommunityPhone() {
        return communityPhone;
    }

    public void setCommunityPhone(String communityPhone) {
        this.communityPhone = communityPhone;
    }

    public boolean getCanRequestRide() {
        return canRequestRide;
    }

    public void setCanRequestRide(boolean canRequestRide) {
        this.canRequestRide = canRequestRide;
    }

    public boolean getCanViewRideHistory() {
        return canViewRideHistory;
    }

    public void setCanViewRideHistory(boolean canViewRideHistory) {
        this.canViewRideHistory = canViewRideHistory;
    }

    public ClientAssociatedContactDto getAssociatedContact() {
        return associatedContact;
    }

    public void setAssociatedContact(ClientAssociatedContactDto associatedContact) {
        this.associatedContact = associatedContact;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getDeactivatedDate() {
        return deactivatedDate;
    }

    public void setDeactivatedDate(Long deactivatedDate) {
        this.deactivatedDate = deactivatedDate;
    }

    public boolean isManuallyCreated() {
        return manuallyCreated;
    }

    public void setManuallyCreated(boolean manuallyCreated) {
        this.manuallyCreated = manuallyCreated;
    }

    public boolean isCanEditSsn() {
        return canEditSsn;
    }

    public void setCanEditSsn(boolean canEditSsn) {
        this.canEditSsn = canEditSsn;
    }

    public String getPharmacyPid() {
        return pharmacyPid;
    }

    public void setPharmacyPid(String pharmacyPid) {
        this.pharmacyPid = pharmacyPid;
    }

    public PrimaryContactDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(PrimaryContactDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }

    public String getHieConsentPolicyTitle() {
        return hieConsentPolicyTitle;
    }

    public void setHieConsentPolicyTitle(String hieConsentPolicyTitle) {
        this.hieConsentPolicyTitle = hieConsentPolicyTitle;
    }

    public Long getHieConsentPolicyObtainedDate() {
        return hieConsentPolicyObtainedDate;
    }

    public void setHieConsentPolicyObtainedDate(Long hieConsentPolicyObtainedDate) {
        this.hieConsentPolicyObtainedDate = hieConsentPolicyObtainedDate;
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

    public String getPointClickCareMedicalRecordNumber() {
        return pointClickCareMedicalRecordNumber;
    }

    public void setPointClickCareMedicalRecordNumber(String pointClickCareMedicalRecordNumber) {
        this.pointClickCareMedicalRecordNumber = pointClickCareMedicalRecordNumber;
    }
}
