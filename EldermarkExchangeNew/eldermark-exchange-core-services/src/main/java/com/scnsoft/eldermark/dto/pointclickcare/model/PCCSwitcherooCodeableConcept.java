package com.scnsoft.eldermark.dto.pointclickcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PCCSwitcherooCodeableConcept {

    private List<PCCSwitcherooCode> codings;

    public List<PCCSwitcherooCode> getCodings() {
        return codings;
    }

    public void setCodings(List<PCCSwitcherooCode> codings) {
        this.codings = codings;
    }
}
