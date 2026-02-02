package com.scnsoft.eldermark.mobile.dto.careteam.invitation;

public class ClientCareTeamInvitationValidationDto {
    private boolean valid;
    private String errorMessage;
    private boolean canEditHieConsent;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isCanEditHieConsent() {
        return canEditHieConsent;
    }

    public void setCanEditHieConsent(boolean canEditHieConsent) {
        this.canEditHieConsent = canEditHieConsent;
    }
}
