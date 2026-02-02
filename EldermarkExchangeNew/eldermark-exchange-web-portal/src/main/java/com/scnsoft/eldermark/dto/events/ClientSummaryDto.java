package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.ClientSummaryViewData;

import java.util.List;

public class ClientSummaryDto implements ClientSummaryViewData {
    private Long id;
    private List<String> identifiers;
    private List<String> aliases;

    private Long organizationId;
    private String organizationTitle;

    private Long communityId;
    private String communityTitle;

    private String firstName;
    private String lastName;
    private String fullName;

    private String ssn;

    private String birthDate;
    private Long deathDate;

    private String gender;
    private String maritalStatus;
    private String primaryLanguage;
    private String race;
    private String clientAccountNumber;
    private String ethnicGroup;
    private String nationality;
    private String religion;
    private List<String> citizenships;
    private String veteranStatus;

    private AddressDto address;

    private String homePhone;
    private String businessPhone;
    private Boolean isActive;

    private String maidenName;

    private List<String> languages;

    private String preferredName;

    private String prefix;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getOrganizationTitle() {
        return organizationTitle;
    }

    @Override
    public void setOrganizationTitle(String organizationTitle) {
        this.organizationTitle = organizationTitle;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    @Override
    public String getCommunityTitle() {
        return communityTitle;
    }

    @Override
    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
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

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getSsn() {
        return ssn;
    }

    @Override
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Override
    public String getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public Long getDeathDate() {
        return deathDate;
    }

    @Override
    public void setDeathDate(Long deathDate) {
        this.deathDate = deathDate;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getMaritalStatus() {
        return maritalStatus;
    }

    @Override
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    @Override
    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    @Override
    public String getRace() {
        return race;
    }

    @Override
    public void setRace(String race) {
        this.race = race;
    }

    @Override
    public String getClientAccountNumber() {
        return clientAccountNumber;
    }

    @Override
    public void setClientAccountNumber(String clientAccountNumber) {
        this.clientAccountNumber = clientAccountNumber;
    }

    @Override
    public String getEthnicGroup() {
        return ethnicGroup;
    }

    @Override
    public void setEthnicGroup(String ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    @Override
    public String getNationality() {
        return nationality;
    }

    @Override
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String getReligion() {
        return religion;
    }

    @Override
    public void setReligion(String religion) {
        this.religion = religion;
    }

    @Override
    public List<String> getCitizenships() {
        return citizenships;
    }

    @Override
    public void setCitizenships(List<String> citizenships) {
        this.citizenships = citizenships;
    }

    @Override
    public String getVeteranStatus() {
        return veteranStatus;
    }

    @Override
    public void setVeteranStatus(String veteranStatus) {
        this.veteranStatus = veteranStatus;
    }

    @Override
    public AddressDto getAddress() {
        return address;
    }

    @Override
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
    public String getHomePhone() {
        return homePhone;
    }

    @Override
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    @Override
    public String getBusinessPhone() {
        return businessPhone;
    }

    @Override
    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    @Override
    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String getMaidenName() {
        return maidenName;
    }

    @Override
    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    @Override
    public List<String> getLanguages() {
        return languages;
    }

    @Override
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    @Override
    public String getPreferredName() {
        return preferredName;
    }

    @Override
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Boolean getActive() {
        return isActive;
    }

    @Override
    public void setActive(Boolean active) {
        isActive = active;
    }
}
