package com.scnsoft.eldermark.therap.bean;

import org.apache.commons.csv.CSVRecord;

import java.util.Set;

public class TherapEvent extends TherapRecord {

    public TherapEvent(Set<String> headers, CSVRecord csvRecord) {
        super(headers, csvRecord);
    }

    @Override
    protected String getPatientIdHeader() {
        return "INDIVIDUALIDNUMBER";
    }

    @Override
    protected String getFirstNameHeader() {
        return "INDIVIDUALFIRSTNAME";
    }

    @Override
    protected String getLastNameHeader() {
        return "INDIVIDUALLASTNAME";
    }

    @Override
    protected String getSsnHeader() {
        return "INDIVIDUALSSN";
    }

    @Override
    protected String getDateOfBirthHeader() {
        return "INDIVIDUALDATEOFBIRTH";
    }
}
