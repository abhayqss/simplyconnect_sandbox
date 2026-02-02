package com.scnsoft.eldermark.shared.exceptions;

public class DocumentSharePolicyViolation extends LocalizedException {
    public DocumentSharePolicyViolation() {
    }

    public DocumentSharePolicyViolation(Object ...params) {
        super(params);
    }

    public DocumentSharePolicyViolation(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.document.share.policy";
    }
}
