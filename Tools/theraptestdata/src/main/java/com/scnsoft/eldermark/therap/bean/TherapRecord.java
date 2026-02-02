package com.scnsoft.eldermark.therap.bean;

import org.apache.commons.csv.CSVRecord;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class TherapRecord {

    private Map<String, String> valuesMap;

    private String filename;

    public TherapRecord(Set<String> headers, CSVRecord csvRecord) {
        this.valuesMap = new LinkedHashMap<>(headers.size());
        for (String header: headers) {
            valuesMap.put(header, csvRecord.get(header));
        }
    }
    protected abstract String getPatientIdHeader();
    protected abstract String getFirstNameHeader();
    protected abstract String getLastNameHeader();
    protected abstract String getSsnHeader();
    protected abstract String getDateOfBirthHeader();


    public String getPatientId() {
        return valuesMap.get(getPatientIdHeader());
    }

    public void setPatientId(String patientId) {
        valuesMap.put(getPatientIdHeader(), patientId);
    }

    public String getFirstName() {
        return valuesMap.get(getFirstNameHeader());
    }

    public void setFirstName(String firstName) {
        valuesMap.put(getFirstNameHeader(), firstName);
    }


    public String getLastName() {
        return valuesMap.get(getLastNameHeader());
    }

    public void setLastName(String lastName) {
        valuesMap.put(getLastNameHeader(), lastName);
    }


    public String getSSN() {
        return valuesMap.get(getSsnHeader());
    }

    public void setSSN(String ssn) {
        valuesMap.put(getSsnHeader(), ssn != null ? ssn.replace("-", "") : null);
    }


    public String getDateOfBirth() {
        return valuesMap.get(getDateOfBirthHeader());
    }

    public void setDateOfBirth(String dateOfBirth) {
        valuesMap.put(getDateOfBirthHeader(), dateOfBirth);
    }

    public String[] valuesAsStringArray() {
        return valuesMap.values().toArray(new String[0]);
    }


    public Set<String> getHeaders() {
        return valuesMap.keySet();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
