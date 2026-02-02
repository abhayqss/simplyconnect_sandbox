package com.scnsoft.eldermark.api.shared.dto.prospect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.dto.Gender;
import com.scnsoft.eldermark.api.shared.dto.MaritalStatus;
import com.scnsoft.eldermark.api.shared.dto.Race;
import com.scnsoft.eldermark.api.shared.dto.StateAbbrEnum;
import com.scnsoft.eldermark.entity.prospect.RelatedPartyRelationship;
import com.scnsoft.eldermark.entity.prospect.Veteran;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/**
 * This dto represents Prospect data
 */
@ApiModel(description = "This dto represents Prospect data")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2022-12-08T13:58:54.417+02:00")
public class ProspectDto {

  private Long id = null;

  private Long communityId = null;

  @JsonProperty("identifier")
  private Long identifier = null;

  @JsonProperty("organizationOID")
  private String organizationOID = null;

  @JsonProperty("communityOID")
  private String communityOID = null;

  @JsonProperty("firstName")
  private String firstName = null;

  @JsonProperty("lastName")
  private String lastName = null;

  @JsonProperty("middleName")
  private String middleName = null;

  @JsonProperty("inNetworkInsuranceId")
  private Long inNetworkInsuranceId = null;

  @JsonProperty("insurancePlan")
  private String insurancePlan = null;

  @JsonProperty("birthDate")
  private String birthDate = null;

  @JsonProperty("gender")
  private Gender gender = null;

  @JsonProperty("maritalStatus")
  private MaritalStatus maritalStatus = null;

  @JsonProperty("race")
  private Race race = null;

  @JsonProperty("ssn")
  private String ssn = null;

  @JsonProperty("veteran")
  private Veteran veteran = null;

  @JsonProperty("street")
  private String street = null;

  @JsonProperty("city")
  private String city = null;

  @JsonProperty("state")
  private StateAbbrEnum state = null;

  @JsonProperty("zip")
  private String zip = null;

  @JsonProperty("phone")
  private String phone = null;

  @JsonProperty("email")
  private String email = null;

  @JsonProperty("moveInDate")
  private Long moveInDate = null;

  @JsonProperty("rentalAgreementDate")
  private Long rentalAgreementDate = null;

  @JsonProperty("assessmentDate")
  private Long assessmentDate = null;

  @JsonProperty("referralSource")
  private String referralSource = null;

  @JsonProperty("notes")
  private String notes = null;

  @JsonProperty("relatedPartyFirstName")
  private String relatedPartyFirstName = null;

  @JsonProperty("relatedPartyLastName")
  private String relatedPartyLastName = null;

  @JsonProperty("relatedPartyRelationship")
  private RelatedPartyRelationship relatedPartyRelationship = null;

  @JsonProperty("relatedPartyStreet")
  private String relatedPartyStreet = null;

  @JsonProperty("relatedPartyCity")
  private String relatedPartyCity = null;

  @JsonProperty("relatedPartyState")
  private StateAbbrEnum relatedPartyState = null;

  @JsonProperty("relatedPartyZip")
  private String relatedPartyZip = null;

  @JsonProperty("relatedPartyPhone")
  private String relatedPartyPhone = null;

  @JsonProperty("relatedPartyEmail")
  private String relatedPartyEmail = null;

  @JsonProperty("isRelatedPartySecondOccupant")
  private Boolean isRelatedPartySecondOccupant = null;

  @JsonProperty("secondOccupantFirstName")
  private String secondOccupantFirstName = null;

  @JsonProperty("secondOccupantLastName")
  private String secondOccupantLastName = null;

  @JsonProperty("secondOccupantMiddleName")
  private String secondOccupantMiddleName = null;

  @JsonProperty("secondOccupantInNetworkInsuranceId")
  private Long secondOccupantInNetworkInsuranceId = null;

  @JsonProperty("secondOccupantInsurancePlan")
  private String secondOccupantInsurancePlan = null;

  @JsonProperty("secondOccupantBirthDate")
  private String secondOccupantBirthDate = null;

  @JsonProperty("secondOccupantGender")
  private Gender secondOccupantGender = null;

  @JsonProperty("secondOccupantMaritalStatus")
  private MaritalStatus secondOccupantMaritalStatus = null;

  @JsonProperty("secondOccupantRace")
  private Race secondOccupantRace = null;

  @JsonProperty("secondOccupantSsn")
  private String secondOccupantSsn = null;

  @JsonProperty("secondOccupantVeteran")
  private Veteran secondOccupantVeteran = null;

  @JsonProperty("useProspectAddressAsSecondOccupantAddress")
  private Boolean useProspectAddressAsSecondOccupantAddress = null;

  @JsonProperty("secondOccupantStreet")
  private String secondOccupantStreet = null;

  @JsonProperty("secondOccupantCity")
  private String secondOccupantCity = null;

  @JsonProperty("secondOccupantState")
  private StateAbbrEnum secondOccupantState = null;

  @JsonProperty("secondOccupantZip")
  private String secondOccupantZip = null;

  @JsonProperty("secondOccupantPhone")
  private String secondOccupantPhone = null;

  @JsonProperty("secondOccupantEmail")
  private String secondOccupantEmail = null;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCommunityId() {
    return communityId;
  }

  public void setCommunityId(Long communityId) {
    this.communityId = communityId;
  }

  /**
   * Uniqie prospect identifier in external system
   * @return identifier
   */
  @ApiModelProperty(example = "72", required = true, value = "Uniqie prospect identifier in external system")
  @NotNull
  public Long getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Long identifier) {
    this.identifier = identifier;
  }

  /**
   * Organization OID in Simply Connect
   * @return corganizationOID
   */
  @ApiModelProperty(example = "1.2.34567781234.6.7.8.9", required = true, value = "Organization OID in Simply Connect")
  @NotNull
  public String getOrganizationOID() {
    return organizationOID;
  }

  public void setOrganizationOID(String organizationOID) {
    this.organizationOID = organizationOID;
  }

  /**
   * Community OID in Simply Connect
   * @return communityOID
   */
  @ApiModelProperty(example = "1.2.34567781234.6.7.8.9", required = true, value = "Community OID in Simply Connect")
  @NotNull
  public String getCommunityOID() {
    return communityOID;
  }

  public void setCommunityOID(String communityOID) {
    this.communityOID = communityOID;
  }

  /**
   * Prospect's first name
   * @return firstName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Prospect's first name
   * @return lastName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Prospect's first name
   * @return middleName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * InNetwork insurance id in Simply Connect
   * @return inNetworkInsuranceId
   */
  @ApiModelProperty(value = "InNetwork insurance id in Simply Connect")
  public Long getInNetworkInsuranceId() {
    return inNetworkInsuranceId;
  }

  public void setInNetworkInsuranceId(Long inNetworkInsuranceId) {
    this.inNetworkInsuranceId = inNetworkInsuranceId;
  }

  /**
   * Insurance plan name
   * @return insurancePlan
   */
  @ApiModelProperty(value = "Insurance plan name")
  public String getInsurancePlan() {
    return insurancePlan;
  }

  public void setInsurancePlan(String insurancePlan) {
    this.insurancePlan = insurancePlan;
  }

  /**
   * Date of birth in format MM/DD/YYYY
   * @return birthDate
   */
  @ApiModelProperty(example = "10/20/1960", value = "Date of birth in format MM/DD/YYYY")
  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

    @ApiModelProperty(value = "")
  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

    @ApiModelProperty(value = "")
  public MaritalStatus getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(MaritalStatus maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

    @ApiModelProperty(value = "")
  public Race getRace() {
    return race;
  }

  public void setRace(Race race) {
    this.race = race;
  }

  /**
   * Prospect's ssn
   * @return ssn
   */
  @ApiModelProperty(value = "Prospect's ssn")
  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

    @ApiModelProperty(value = "")
  public Veteran getVeteran() {
    return veteran;
  }

  public void setVeteran(Veteran veteran) {
    this.veteran = veteran;
  }

    @ApiModelProperty(value = "")
  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

    @ApiModelProperty(value = "")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

    @ApiModelProperty(value = "")
  public StateAbbrEnum getState() {
    return state;
  }

  public void setState(StateAbbrEnum state) {
    this.state = state;
  }

    @ApiModelProperty(value = "")
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

    @ApiModelProperty(value = "")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

    @ApiModelProperty(value = "")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

    @ApiModelProperty(example = "1326862800000", value = "")
  public Long getMoveInDate() {
    return moveInDate;
  }

  public void setMoveInDate(Long moveInDate) {
    this.moveInDate = moveInDate;
  }

    @ApiModelProperty(example = "1326862800000", value = "")
  public Long getRentalAgreementDate() {
    return rentalAgreementDate;
  }

  public void setRentalAgreementDate(Long rentalAgreementDate) {
    this.rentalAgreementDate = rentalAgreementDate;
  }

    @ApiModelProperty(example = "1326862800000", value = "")
  public Long getAssessmentDate() {
    return assessmentDate;
  }

  public void setAssessmentDate(Long assessmentDate) {
    this.assessmentDate = assessmentDate;
  }

    @ApiModelProperty(value = "")
  public String getReferralSource() {
    return referralSource;
  }

  public void setReferralSource(String referralSource) {
    this.referralSource = referralSource;
  }

    @ApiModelProperty(value = "")
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Prospect's related party first name
   * @return relatedPartyFirstName
   */
  @ApiModelProperty(value = "Prospect's related party first name")
  public String getRelatedPartyFirstName() {
    return relatedPartyFirstName;
  }

  public void setRelatedPartyFirstName(String relatedPartyFirstName) {
    this.relatedPartyFirstName = relatedPartyFirstName;
  }

  /**
   * Prospect's related party first name
   * @return relatedPartyLastName
   */
  @ApiModelProperty(value = "Prospect's related party first name")
  public String getRelatedPartyLastName() {
    return relatedPartyLastName;
  }

  public void setRelatedPartyLastName(String relatedPartyLastName) {
    this.relatedPartyLastName = relatedPartyLastName;
  }

    @ApiModelProperty(value = "")
  public RelatedPartyRelationship getRelatedPartyRelationship() {
    return relatedPartyRelationship;
  }

  public void setRelatedPartyRelationship(RelatedPartyRelationship relatedPartyRelationship) {
    this.relatedPartyRelationship = relatedPartyRelationship;
  }

    @ApiModelProperty(value = "")
  public String getRelatedPartyStreet() {
    return relatedPartyStreet;
  }

  public void setRelatedPartyStreet(String relatedPartyStreet) {
    this.relatedPartyStreet = relatedPartyStreet;
  }

    @ApiModelProperty(value = "")
  public String getRelatedPartyCity() {
    return relatedPartyCity;
  }

  public void setRelatedPartyCity(String relatedPartyCity) {
    this.relatedPartyCity = relatedPartyCity;
  }

    @ApiModelProperty(value = "")
  public StateAbbrEnum getRelatedPartyState() {
    return relatedPartyState;
  }

  public void setRelatedPartyState(StateAbbrEnum relatedPartyState) {
    this.relatedPartyState = relatedPartyState;
  }

    @ApiModelProperty(value = "")
  public String getRelatedPartyZip() {
    return relatedPartyZip;
  }

  public void setRelatedPartyZip(String relatedPartyZip) {
    this.relatedPartyZip = relatedPartyZip;
  }

    @ApiModelProperty(value = "")
  public String getRelatedPartyPhone() {
    return relatedPartyPhone;
  }

  public void setRelatedPartyPhone(String relatedPartyPhone) {
    this.relatedPartyPhone = relatedPartyPhone;
  }

    @ApiModelProperty(value = "")
  public String getRelatedPartyEmail() {
    return relatedPartyEmail;
  }

  public void setRelatedPartyEmail(String relatedPartyEmail) {
    this.relatedPartyEmail = relatedPartyEmail;
  }

    @ApiModelProperty(value = "")
  public Boolean getIsRelatedPartySecondOccupant() {
    return isRelatedPartySecondOccupant;
  }

  public void setIsRelatedPartySecondOccupant(Boolean isRelatedPartySecondOccupant) {
    this.isRelatedPartySecondOccupant = isRelatedPartySecondOccupant;
  }

  /**
   * Prospect's first name
   * @return secondOccupantFirstName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getSecondOccupantFirstName() {
    return secondOccupantFirstName;
  }

  public void setSecondOccupantFirstName(String secondOccupantFirstName) {
    this.secondOccupantFirstName = secondOccupantFirstName;
  }

  /**
   * Prospect's first name
   * @return secondOccupantLastName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getSecondOccupantLastName() {
    return secondOccupantLastName;
  }

  public void setSecondOccupantLastName(String secondOccupantLastName) {
    this.secondOccupantLastName = secondOccupantLastName;
  }

  /**
   * Prospect's first name
   * @return secondOccupantMiddleName
   */
  @ApiModelProperty(value = "Prospect's first name")
  public String getSecondOccupantMiddleName() {
    return secondOccupantMiddleName;
  }

  public void setSecondOccupantMiddleName(String secondOccupantMiddleName) {
    this.secondOccupantMiddleName = secondOccupantMiddleName;
  }

  /**
   * InNetwork insurance id in Simply Connect
   * @return secondOccupantInNetworkInsuranceId
   */
  @ApiModelProperty(value = "InNetwork insurance id in Simply Connect")
  public Long getSecondOccupantInNetworkInsuranceId() {
    return secondOccupantInNetworkInsuranceId;
  }

  public void setSecondOccupantInNetworkInsuranceId(Long secondOccupantInNetworkInsuranceId) {
    this.secondOccupantInNetworkInsuranceId = secondOccupantInNetworkInsuranceId;
  }

  /**
   * Insurance plan name
   * @return secondOccupantInsurancePlan
   */
  @ApiModelProperty(value = "Insurance plan name")
  public String getSecondOccupantInsurancePlan() {
    return secondOccupantInsurancePlan;
  }

  public void setSecondOccupantInsurancePlan(String secondOccupantInsurancePlan) {
    this.secondOccupantInsurancePlan = secondOccupantInsurancePlan;
  }

  /**
   * Date of birth in format MM/DD/YYYY
   * @return secondOccupantBirthDate
   */
  @ApiModelProperty(example = "10/20/1960", value = "Date of birth in format MM/DD/YYYY")
  public String getSecondOccupantBirthDate() {
    return secondOccupantBirthDate;
  }

  public void setSecondOccupantBirthDate(String secondOccupantBirthDate) {
    this.secondOccupantBirthDate = secondOccupantBirthDate;
  }

    @ApiModelProperty(value = "")
  public Gender getSecondOccupantGender() {
    return secondOccupantGender;
  }

  public void setSecondOccupantGender(Gender secondOccupantGender) {
    this.secondOccupantGender = secondOccupantGender;
  }

    @ApiModelProperty(value = "")
  public MaritalStatus getSecondOccupantMaritalStatus() {
    return secondOccupantMaritalStatus;
  }

  public void setSecondOccupantMaritalStatus(MaritalStatus secondOccupantMaritalStatus) {
    this.secondOccupantMaritalStatus = secondOccupantMaritalStatus;
  }

    @ApiModelProperty(value = "")
  public Race getSecondOccupantRace() {
    return secondOccupantRace;
  }

  public void setSecondOccupantRace(Race secondOccupantRace) {
    this.secondOccupantRace = secondOccupantRace;
  }

  /**
   * Prospect's ssn
   * @return secondOccupantSsn
   */
  @ApiModelProperty(value = "Prospect's ssn")
  public String getSecondOccupantSsn() {
    return secondOccupantSsn;
  }

  public void setSecondOccupantSsn(String secondOccupantSsn) {
    this.secondOccupantSsn = secondOccupantSsn;
  }

    @ApiModelProperty(value = "")
  public Veteran getSecondOccupantVeteran() {
    return secondOccupantVeteran;
  }

  public void setSecondOccupantVeteran(Veteran secondOccupantVeteran) {
    this.secondOccupantVeteran = secondOccupantVeteran;
  }

    @ApiModelProperty(value = "")
  public Boolean getUseProspectAddressAsSecondOccupantAddress() {
    return useProspectAddressAsSecondOccupantAddress;
  }

  public void setUseProspectAddressAsSecondOccupantAddress(Boolean useProspectAddressAsSecondOccupantAddress) {
    this.useProspectAddressAsSecondOccupantAddress = useProspectAddressAsSecondOccupantAddress;
  }

    @ApiModelProperty(value = "")
  public String getSecondOccupantStreet() {
    return secondOccupantStreet;
  }

  public void setSecondOccupantStreet(String secondOccupantStreet) {
    this.secondOccupantStreet = secondOccupantStreet;
  }

    @ApiModelProperty(value = "")
  public String getSecondOccupantCity() {
    return secondOccupantCity;
  }

  public void setSecondOccupantCity(String secondOccupantCity) {
    this.secondOccupantCity = secondOccupantCity;
  }

    @ApiModelProperty(value = "")
  public StateAbbrEnum getSecondOccupantState() {
    return secondOccupantState;
  }

  public void setSecondOccupantState(StateAbbrEnum secondOccupantState) {
    this.secondOccupantState = secondOccupantState;
  }

    @ApiModelProperty(value = "")
  public String getSecondOccupantZip() {
    return secondOccupantZip;
  }

  public void setSecondOccupantZip(String secondOccupantZip) {
    this.secondOccupantZip = secondOccupantZip;
  }

    @ApiModelProperty(value = "")
  public String getSecondOccupantPhone() {
    return secondOccupantPhone;
  }

  public void setSecondOccupantPhone(String secondOccupantPhone) {
    this.secondOccupantPhone = secondOccupantPhone;
  }

    @ApiModelProperty(value = "")
  public String getSecondOccupantEmail() {
    return secondOccupantEmail;
  }

  public void setSecondOccupantEmail(String secondOccupantEmail) {
    this.secondOccupantEmail = secondOccupantEmail;
  }

}

