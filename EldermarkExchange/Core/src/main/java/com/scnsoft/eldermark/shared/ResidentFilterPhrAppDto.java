package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;

import java.util.Date;

public class ResidentFilterPhrAppDto implements ResidentFilter {
    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String ssn;

    private Database database;

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
    public String getMiddleName() {
        return null;
    }

    @Override
    public Gender getGender() {
        return null;
    }

    @Override
    public Date getDateOfBirth() {
        return null;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getPostalCode() {
        return null;
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
        return null;
    }

    @Override
    public String getStreet() {
        return null;
    }

    @Override
    public SearchMode getMode() {
        return SearchMode.MATCH_ALL;
    }

    @Override
    public MatchStatus getMatchStatus() {
        return null;
    }

    @Override
    public MergeStatus getMergeStatus() {
        return null;
    }

    @Override
    public String getProviderOrganization() {
        return null;
    }

    @Override
    public String getCommunity() {
        return null;
    }

    @Override
    public String toString() {
        return "ResidentFilterPhrAppDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", ssn=" + ssn +
                ", database=" + database +
                '}';
    }
}
