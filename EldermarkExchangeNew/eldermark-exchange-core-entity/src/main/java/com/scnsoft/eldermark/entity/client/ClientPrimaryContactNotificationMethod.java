package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;

public enum ClientPrimaryContactNotificationMethod {

    EMAIL("Email", SignatureRequestNotificationMethod.EMAIL),
    PHONE("Phone", SignatureRequestNotificationMethod.SMS),
    CHAT("Chat", SignatureRequestNotificationMethod.CHAT);

    private final String displayName;
    private final SignatureRequestNotificationMethod method;

    ClientPrimaryContactNotificationMethod(String displayName, SignatureRequestNotificationMethod method) {
        this.displayName = displayName;
        this.method = method;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SignatureRequestNotificationMethod getMethod() {
        return method;
    }
}
