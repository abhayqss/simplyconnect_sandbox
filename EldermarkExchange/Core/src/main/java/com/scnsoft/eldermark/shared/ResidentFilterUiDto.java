package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class ResidentFilterUiDto implements ResidentFilter, Serializable {

    @NotEmpty
    @NotNull
    private String firstName;

    @NotEmpty
    @NotNull
    private String lastName;

    @NotNull
    private Gender gender;

    private String middleName;

    @DateTimeFormat(pattern="MM/dd/yyyy")
    @Past
    private Date dateOfBirth;

    private String phone;

    private String city;

    private String street;

    private String state;

    private String postalCode;

    private String ssn;

    private String lastFourDigitsOfSsn;

    private Set<SearchScope> searchScopes;

    private boolean ssnRequired;

    private boolean dateOfBirthRequired;

    private SearchMode mode = SearchMode.MATCH_ALL;

    private MergeStatus mergeStatus;

    private MatchStatus matchStatus;

    private String community;

    private String providerOrganization;

    public ResidentFilterUiDto() {
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Override
    public String getLastFourDigitsOfSsn() {
        return lastFourDigitsOfSsn;
    }

    public void setLastFourDigitsOfSsn(String lastFourDigitsOfSsn) {
        this.lastFourDigitsOfSsn = lastFourDigitsOfSsn;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<SearchScope> getSearchScopes() {
        return searchScopes;
    }

    public void setSearchScopes(Set<SearchScope> searchScopes) {
        this.searchScopes = searchScopes;
    }

    public boolean isSsnRequired() {
        return ssnRequired;
    }

    public void setSsnRequired(boolean isSsnRequired) {
        this.ssnRequired = isSsnRequired;
    }

    public boolean isDateOfBirthRequired() {
        return dateOfBirthRequired;
    }

    public void setDateOfBirthRequired(boolean isDateOfBirthRequired) {
        this.dateOfBirthRequired = isDateOfBirthRequired;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public SearchMode getMode() {
        return mode;
    }

    public void setMode(SearchMode mode) {
        this.mode = mode;
    }

    @Override
    public MergeStatus getMergeStatus() {
        return mergeStatus;
    }

    public void setMergeStatus(MergeStatus mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    @Override
    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    @Override
    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @Override
    public String getProviderOrganization() {
        return providerOrganization;
    }

    public void setProviderOrganization(String organization) {
        this.providerOrganization = organization;
    }

    @Override
    public String toString() {
        return "ResidentFilterUiDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", middleName='" + middleName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", ssn='" + ssn + '\'' +
                ", mode=" + mode +
                ", mergeStatus=" + mergeStatus +
                ", matchStatus=" + matchStatus +
                ", community='" + community + '\'' +
                ", providerOrganization='" + providerOrganization + '\'' +
                '}';
    }
}
