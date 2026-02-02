package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.validation.ValidationGroups;

import javax.validation.constraints.NotNull;

public class OrganizationFeaturesDto implements com.scnsoft.eldermark.dto.organization.OrganizationFeatures {

    private Boolean canEdit;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean isChatEnabled;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean isVideoEnabled;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean isSignatureEnabled;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean areComprehensiveAssessmentsEnabled;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean isPaperlessHealthcareEnabled;

    @NotNull(groups = ValidationGroups.OrganizationFeatures.class)
    private Boolean areAppointmentsEnabled;

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public Boolean getIsChatEnabled() {
        return isChatEnabled;
    }

    public void setIsChatEnabled(Boolean chatEnabled) {
        isChatEnabled = chatEnabled;
    }

    @Override
    public Boolean getIsVideoEnabled() {
        return isVideoEnabled;
    }

    public void setIsVideoEnabled(Boolean videoEnabled) {
        isVideoEnabled = videoEnabled;
    }

    @Override
    public Boolean getAreComprehensiveAssessmentsEnabled() {
        return areComprehensiveAssessmentsEnabled;
    }

    public void setAreComprehensiveAssessmentsEnabled(Boolean areComprehensiveAssessmentsEnabled) {
        this.areComprehensiveAssessmentsEnabled = areComprehensiveAssessmentsEnabled;
    }

    @Override
    public Boolean getIsPaperlessHealthcareEnabled() {
        return isPaperlessHealthcareEnabled;
    }

    public void setIsPaperlessHealthcareEnabled(Boolean paperlessHealthcareEnabled) {
        isPaperlessHealthcareEnabled = paperlessHealthcareEnabled;
    }

    public Boolean getAreAppointmentsEnabled() {
        return areAppointmentsEnabled;
    }

    public void setAreAppointmentsEnabled(Boolean appointmentsEnabled) {
        areAppointmentsEnabled = appointmentsEnabled;
    }

    @Override
    public Boolean getIsSignatureEnabled() {
        return isSignatureEnabled;
    }

    public void setIsSignatureEnabled(Boolean signatureEnabled) {
        isSignatureEnabled = signatureEnabled;
    }
}
