package com.scnsoft.eldermark.shared.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created by
 * User: stsiushkevich
 * Date: 31-03-2015
 * Time: 6:24 AM
 */
public class MessagingSetupForm {
    private String pin;
    private CommonsMultipartFile keystore;
    private Boolean isConfigured;
    private String certificateName;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public CommonsMultipartFile getKeystore() {
        return keystore;
    }

    public void setKeystore(CommonsMultipartFile keystore) {
        this.keystore = keystore;
    }

    public Boolean getConfigured() {
        return isConfigured;
    }

    public void setConfigured(Boolean configured) {
        isConfigured = configured;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }
}
