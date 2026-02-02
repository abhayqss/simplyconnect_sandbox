package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentAgencyAndStaffDto {

    @Size(max = 256)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String agencyName;

    @Size(max = 256)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String agencyAddress;

    @Size(max = 256)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String staffServiceCoordinatorName;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String staffPhone;

    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String staffEmail;

    @Size(max = 256)
    @NotEmpty(groups = ValidationGroups.Update.class)
    private String staffQualityAdminName;

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyAddress() {
        return agencyAddress;
    }

    public void setAgencyAddress(String agencyAddress) {
        this.agencyAddress = agencyAddress;
    }

    public String getStaffServiceCoordinatorName() {
        return staffServiceCoordinatorName;
    }

    public void setStaffServiceCoordinatorName(String staffServiceCoordinatorName) {
        this.staffServiceCoordinatorName = staffServiceCoordinatorName;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffQualityAdminName() {
        return staffQualityAdminName;
    }

    public void setStaffQualityAdminName(String staffQualityAdminName) {
        this.staffQualityAdminName = staffQualityAdminName;
    }
}
