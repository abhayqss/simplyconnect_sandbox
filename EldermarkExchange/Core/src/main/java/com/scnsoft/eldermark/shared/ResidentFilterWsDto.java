package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResidentFilter")
public class ResidentFilterWsDto implements ResidentFilter {
    @XmlElement(name="firstName", required = true)
    private String firstName;

    @XmlElement(name = "lastName", required = true)
    private String lastName;

    @XmlElement(name = "gender", required = true)
    private Gender gender;

    @XmlElement(name = "middleName")
    private String middleName;

    @XmlElement(name = "dateOfBirth")
    @XmlSchemaType(name = "date", type=javax.xml.datatype.XMLGregorianCalendar.class)
    private XMLGregorianCalendar dateOfBirth;

    @XmlElement(name = "phone")
    private String phone;

    @XmlElement(name = "city")
    private String city;

    @XmlElement(name = "state")
    private String state;

    @XmlElement(name = "postalCode")
    private String postalCode;

    @XmlElement(name = "lastFourDigitsOfSsn")
    private String ssn;

    @XmlElement(name = "street")
    private String street;

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
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public Date getDateOfBirth() {
        if (dateOfBirth == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(dateOfBirth.getYear(), dateOfBirth.getMonth() - 1, dateOfBirth.getDay());
        return cal.getTime();
    }

    public void setDateOfBirth(XMLGregorianCalendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
        return null;
    }

    @Override
    public Database getDatabase() {
        return null;
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

    @Override
    public String getLastFourDigitsOfSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Override
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

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
        return "ResidentFilterWsDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", middleName='" + middleName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phone='" + phone + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", ssn=" + ssn +
                '}';
    }
}
