package com.scnsoft.eldermark.therap.bean;

import org.apache.commons.csv.CSVRecord;

import java.util.Set;

public class TherapIdf extends TherapRecord {


    public TherapIdf(Set<String> headers, CSVRecord csvRecord) {
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
        return "SSN";
    }

    @Override
    protected String getDateOfBirthHeader() {
        return "BIRTHDATE";
    }
}
