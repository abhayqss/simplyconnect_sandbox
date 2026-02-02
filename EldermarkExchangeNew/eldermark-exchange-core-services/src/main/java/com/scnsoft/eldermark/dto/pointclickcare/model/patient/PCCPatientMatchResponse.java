package com.scnsoft.eldermark.dto.pointclickcare.model.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PCCPatientMatchResponse {

    private List<PCCPatientMatch> data;

    public List<PCCPatientMatch> getData() {
        return data;
    }

    public void setData(List<PCCPatientMatch> data) {
        this.data = data;
    }
}
