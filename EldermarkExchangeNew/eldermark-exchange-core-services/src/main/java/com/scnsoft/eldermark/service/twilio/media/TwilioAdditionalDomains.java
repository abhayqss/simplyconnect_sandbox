package com.scnsoft.eldermark.service.twilio.media;

public enum TwilioAdditionalDomains {
    MEDIA("mcs.us1");

    private final String value;

    private TwilioAdditionalDomains(final String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
