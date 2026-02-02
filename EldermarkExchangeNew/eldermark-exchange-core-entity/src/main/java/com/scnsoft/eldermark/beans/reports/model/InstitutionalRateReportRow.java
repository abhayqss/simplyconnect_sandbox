package com.scnsoft.eldermark.beans.reports.model;

public class InstitutionalRateReportRow {

    private String community;

    private Long activeResidentCount;
    private Long erVisitCount;
    private Long snfInstitutionalizationCount;
    private Long hospitalizationCount;

    private Float institutionalRate;
    private Float erRate;
    private Float snfRate;
    private Float hospitalRate;

    public InstitutionalRateReportRow(
        String community,
        Long activeResidentCount,
        Long erVisitCount,
        Long snfInstitutionalizationCount,
        Long hospitalizationCount,
        Float institutionalRate,
        Float erRate,
        Float snfRate,
        Float hospitalRate
    ) {
        this.community = community;
        this.activeResidentCount = activeResidentCount;
        this.erVisitCount = erVisitCount;
        this.snfInstitutionalizationCount = snfInstitutionalizationCount;
        this.hospitalizationCount = hospitalizationCount;
        this.institutionalRate = institutionalRate;
        this.erRate = erRate;
        this.snfRate = snfRate;
        this.hospitalRate = hospitalRate;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getActiveResidentCount() {
        return activeResidentCount;
    }

    public void setActiveResidentCount(Long activeResidentCount) {
        this.activeResidentCount = activeResidentCount;
    }

    public Long getErVisitCount() {
        return erVisitCount;
    }

    public void setErVisitCount(Long erVisitCount) {
        this.erVisitCount = erVisitCount;
    }

    public Long getSnfInstitutionalizationCount() {
        return snfInstitutionalizationCount;
    }

    public void setSnfInstitutionalizationCount(Long snfInstitutionalizationCount) {
        this.snfInstitutionalizationCount = snfInstitutionalizationCount;
    }

    public Long getHospitalizationCount() {
        return hospitalizationCount;
    }

    public void setHospitalizationCount(Long hospitalizationCount) {
        this.hospitalizationCount = hospitalizationCount;
    }

    public Float getInstitutionalRate() {
        return institutionalRate;
    }

    public void setInstitutionalRate(Float institutionalRate) {
        this.institutionalRate = institutionalRate;
    }

    public Float getErRate() {
        return erRate;
    }

    public void setErRate(Float erRate) {
        this.erRate = erRate;
    }

    public Float getSnfRate() {
        return snfRate;
    }

    public void setSnfRate(Float snfRate) {
        this.snfRate = snfRate;
    }

    public Float getHospitalRate() {
        return hospitalRate;
    }

    public void setHospitalRate(Float hospitalRate) {
        this.hospitalRate = hospitalRate;
    }
}
