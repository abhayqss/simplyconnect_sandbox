package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentWitnessDto extends IncidentIndividualDto {

    @Size(max = 20_000)
    private String report;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
