package com.scnsoft.eldermark.shared;

import java.io.Serializable;

public class DirectConfigurationDto implements Serializable {

    private String pin;

    private String keystoreName;

    private Boolean isConfigured;

    public DirectConfigurationDto() {
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getKeystoreName() {
        return keystoreName;
    }

    public void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;
    }

    public Boolean getIsConfigured() {
        return isConfigured;
    }

    public void setIsConfigured(Boolean configured) {
        this.isConfigured = configured;
    }
}
