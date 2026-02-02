package com.scnsoft.exchange.audit.model;


public class MpiReportEntry implements ReportDto {
    private String stateName;
    private Long residentNumber;
    private Long patientDiscoveryNumber;
    private Long patientDiscoveryFails;
    private Long generatedCCDNumber;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Long getResidentNumber() {
        return residentNumber;
    }

    public void setResidentNumber(Long residentNumber) {
        this.residentNumber = residentNumber;
    }

    public Long getPatientDiscoveryNumber() {
        return patientDiscoveryNumber;
    }

    public void setPatientDiscoveryNumber(Long patientDiscoveryNumber) {
        this.patientDiscoveryNumber = patientDiscoveryNumber;
    }

    public Long getPatientDiscoveryFails() {
        return patientDiscoveryFails;
    }

    public void setPatientDiscoveryFails(Long patientDiscoveryFails) {
        this.patientDiscoveryFails = patientDiscoveryFails;
    }

    public Long getGeneratedCCDNumber() {
        return generatedCCDNumber;
    }

    public void setGeneratedCCDNumber(Long generatedCCDNumber) {
        this.generatedCCDNumber = generatedCCDNumber;
    }
}
