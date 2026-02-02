package com.scnsoft.eldermark.entity.signature;

public enum SignatureRequestNotificationMethod {
    CHAT("Chat"),
    EMAIL("Email"),
    SMS("Sms"),
    SIGN_NOW("Sign Now");

    private final String title;

    SignatureRequestNotificationMethod(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
