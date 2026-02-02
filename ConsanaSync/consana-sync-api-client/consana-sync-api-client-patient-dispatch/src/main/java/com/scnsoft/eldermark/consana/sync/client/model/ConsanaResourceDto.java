package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaResourceDto {

    private ConsanaOrganizationDto organization;
    private ConsanaPatientDto patient;
    private List<ConsanaPractitionerDto> practitioner;
    private List<ConsanaDetectedIssueDto> detectedIssue;

    public ConsanaOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(ConsanaOrganizationDto organization) {
        this.organization = organization;
    }

    public ConsanaPatientDto getPatient() {
        return patient;
    }

    public void setPatient(ConsanaPatientDto patient) {
        this.patient = patient;
    }

    public List<ConsanaPractitionerDto> getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(List<ConsanaPractitionerDto> practitioner) {
        this.practitioner = practitioner;
    }

    public List<ConsanaDetectedIssueDto> getDetectedIssue() {
        return detectedIssue;
    }

    public void setDetectedIssue(List<ConsanaDetectedIssueDto> detectedIssue) {
        this.detectedIssue = detectedIssue;
    }
}
