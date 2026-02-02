package com.scnsoft.eldermark.exchange.fk;

public class ResidentForeignKeys {
    private Long facilityOrganizationId;
    private Long genderId;
    private Long maritalStatusId;
    private Long raceId;
    private Long religionId;
    private Long primaryLanguageId;

    public Long getFacilityOrganizationId() {
        return facilityOrganizationId;
    }

    public void setFacilityOrganizationId(Long facilityOrganizationId) {
        this.facilityOrganizationId = facilityOrganizationId;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public Long getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Long maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public Long getRaceId() {
        return raceId;
    }

    public void setRaceId(Long raceId) {
        this.raceId = raceId;
    }

    public Long getReligionId() {
        return religionId;
    }

    public void setReligionId(Long religionId) {
        this.religionId = religionId;
    }

    public Long getPrimaryLanguageId() {
        return primaryLanguageId;
    }

    public void setPrimaryLanguageId(Long primaryLanguageId) {
        this.primaryLanguageId = primaryLanguageId;
    }
}
