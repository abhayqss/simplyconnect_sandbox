package com.scnsoft.eldermark.dto;

import java.security.cert.X509Certificate;

public class TlsConnectivityCheckResult {

    private boolean isSuccess;
    private X509Certificate certificate;
    private Integer responseCode;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
}
