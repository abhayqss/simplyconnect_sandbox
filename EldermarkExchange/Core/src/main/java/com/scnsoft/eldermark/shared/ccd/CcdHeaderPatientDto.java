package com.scnsoft.eldermark.shared.ccd;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CcdHeaderPatientDto {
    private PersonDto person;

    private String socialSecurity;

    private String ssnLastFourDigits;

    private String birthDate;

    private String gender;

    private String maritalStatus;

    private String ethnicGroup;

    private String religion;

    private String race;

    private String firstName;

    private String lastName;

    private List<GuardianDto> guardians;

    private List<LanguageDto> languages;

    private OrganizationDto providerOrganization;

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(String ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
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

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public List<GuardianDto> getGuardians() {
        return guardians;
    }

    public void setGuardians(List<GuardianDto> guardians) {
        this.guardians = guardians;
    }

    public List<LanguageDto> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageDto> languages) {
        this.languages = languages;
    }

    public OrganizationDto getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(OrganizationDto providerOrganization) {
        this.providerOrganization = providerOrganization;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
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

    public String getFullName() {
        String result = StringUtils.isNotEmpty(getFirstName()) ? getFirstName() : "";
        if (StringUtils.isNotEmpty(getLastName())) {
            result = StringUtils.isNotEmpty(result) ? result + " " + getLastName() : getLastName();
        }
        return result;
    }
}
