package com.scnsoft.eldermark.entity.signature;

public enum SignatureRequestRecipientType {
    CLIENT("Client"),
    SELF("Self"),
    STAFF("Staff/Family Member");

    private final String title;

    SignatureRequestRecipientType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
