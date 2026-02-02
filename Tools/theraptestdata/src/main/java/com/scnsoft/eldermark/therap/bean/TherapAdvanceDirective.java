package com.scnsoft.eldermark.therap.bean;

import org.apache.commons.csv.CSVRecord;

import java.util.Set;

public class TherapAdvanceDirective extends TherapRecord {

    public TherapAdvanceDirective(Set<String> headers, CSVRecord csvRecord) {
        super(headers, csvRecord);
    }

    @Override
    protected String getPatientIdHeader() {
        return "IDNUMBER";
    }

    @Override
    protected String getFirstNameHeader() {
        return "FIRSTNAME";
    }

    @Override
    protected String getLastNameHeader() {
        return "LASTNAME";
    }

    @Override
    protected String getSsnHeader() {
        return null;
    }

    @Override
    protected String getDateOfBirthHeader() {
        return null;
    }

    public String getSSN() {
        return null;
    }

    public void setSSN(String ssn) {

    }


    public String getDateOfBirth() {
        return null;
    }

    public void setDateOfBirth(String dateOfBirth) {

    }
}
