package com.scnsoft.eldermark.api.shared.dto;

public class PrimaryCarePhysicianDto {

    private String primaryCarePhysician;
    private String primaryCarePhysicianPhone;
    private String primaryCarePhysicianAddress;

    public PrimaryCarePhysicianDto() {
    }

    public PrimaryCarePhysicianDto(PrimaryCarePhysicianDto other) {
        this.setPrimaryCarePhysician(other.getPrimaryCarePhysician());
        this.setPrimaryCarePhysicianPhone(other.getPrimaryCarePhysicianPhone());
        this.setPrimaryCarePhysicianAddress(other.getPrimaryCarePhysicianAddress());
    }

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public String getPrimaryCarePhysicianPhone() {
        return primaryCarePhysicianPhone;
    }

    public void setPrimaryCarePhysicianPhone(String primaryCarePhysicianPhone) {
        this.primaryCarePhysicianPhone = primaryCarePhysicianPhone;
    }

    public String getPrimaryCarePhysicianAddress() {
        return primaryCarePhysicianAddress;
    }

    public void setPrimaryCarePhysicianAddress(String primaryCarePhysicianAddress) {
        this.primaryCarePhysicianAddress = primaryCarePhysicianAddress;
    }
}
