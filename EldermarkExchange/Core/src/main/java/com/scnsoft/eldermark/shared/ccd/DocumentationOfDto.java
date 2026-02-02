package com.scnsoft.eldermark.shared.ccd;

import java.util.List;

public class DocumentationOfDto {
    private String effectiveTimeLow;

    private String effectiveTimeHigh;

    private List<PersonDto> persons;

    public String getEffectiveTimeLow() {
        return effectiveTimeLow;
    }

    public void setEffectiveTimeLow(String effectiveTimeLow) {
        this.effectiveTimeLow = effectiveTimeLow;
    }

    public String getEffectiveTimeHigh() {
        return effectiveTimeHigh;
    }

    public void setEffectiveTimeHigh(String effectiveTimeHigh) {
        this.effectiveTimeHigh = effectiveTimeHigh;
    }

    public List<PersonDto> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDto> persons) {
        this.persons = persons;
    }
}
