package com.scnsoft.eldermark.exception;

public class CareTeamInvitationClientHieConsentValidationException extends BusinessException {

    private final boolean failedThroughMatchingRecord;


    public CareTeamInvitationClientHieConsentValidationException(String message, boolean failedThroughMatchingRecord) {
        super(message);
        this.failedThroughMatchingRecord = failedThroughMatchingRecord;
    }

    public boolean isFailedThroughMatchingRecord() {
        return failedThroughMatchingRecord;
    }
}
