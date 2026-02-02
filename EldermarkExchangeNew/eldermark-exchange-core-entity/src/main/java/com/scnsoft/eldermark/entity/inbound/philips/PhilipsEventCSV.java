package com.scnsoft.eldermark.entity.inbound.philips;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.time.LocalDateTime;

public class PhilipsEventCSV {

    private static final String DATE_PATTERN = "MM/dd/yyyy HH:mm:ss";

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "Created Date")
    private LocalDateTime createdDate;
    @CsvBindByName(column = "ProgramCode")
    private String programCode;
    @CsvBindByName(column = "Patient First Name")
    private String firstName;
    @CsvBindByName(column = "Patient Last Name")
    private String lastName;
    @CsvBindByName(column = "Medical Record Number")
    private String mrn;
    @CsvBindByName(column = "Subscription ID")
    private String subNid;
    @CsvBindByName(column = "Subscriber Situation")
    private String situation;
    @CsvBindByName(column = "Case Outcome")
    private String outcome;
    @CsvBindByName(column = "Daysonservice")
    private String inServiceDays;
    @CsvBindByName(column = "Orgcd")
    private String orgcd;


    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
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

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getSubNid() {
        return subNid;
    }

    public void setSubNid(String subNid) {
        this.subNid = subNid;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getInServiceDays() {
        return inServiceDays;
    }

    public void setInServiceDays(String inServiceDays) {
        this.inServiceDays = inServiceDays;
    }

    @Override
    public String toString() {
        return "PhilipsEventCSV{" +
                "createdDate='" + createdDate + '\'' +
                ", programCode='" + programCode + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mrn='" + mrn + '\'' +
                ", subNid='" + subNid + '\'' +
                ", situation='" + situation + '\'' +
                ", outcome='" + outcome + '\'' +
                ", inServiceDays='" + inServiceDays + '\'' +
                '}';
    }
}
