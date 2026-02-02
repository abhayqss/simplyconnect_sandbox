package com.scnsoft.eldermark.service.document.signature.provider.pdcflow.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class SignatureApiDto {
    private BigInteger signatureId;
    private String signatureUrl;

    private String firstName;
    private String lastName;
    private boolean standaloneSignatureRequested = false;
    private String templateName;
    private int timeoutMinutes;
    private int verificationPin;
    private String description;
    private String postbackUrl;
    private String postbackAuthHeader;
    private DocumentApiDto document;
    private String redirectLink;
    private Boolean requestGeolocation;
    private CompanyOverrideApiDto companyOverride;

    private String errorCode;
    private String errorMessage;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusDate;

    private String modificationCode;
    private String username;

    public BigInteger getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(BigInteger signatureId) {
        this.signatureId = signatureId;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isStandaloneSignatureRequested() {
        return standaloneSignatureRequested;
    }

    public void setStandaloneSignatureRequested(boolean standaloneSignatureRequested) {
        this.standaloneSignatureRequested = standaloneSignatureRequested;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    public int getVerificationPin() {
        return verificationPin;
    }

    public void setVerificationPin(int verificationPin) {
        this.verificationPin = verificationPin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostbackUrl() {
        return postbackUrl;
    }

    public void setPostbackUrl(String postbackUrl) {
        this.postbackUrl = postbackUrl;
    }

    public String getPostbackAuthHeader() {
        return postbackAuthHeader;
    }

    public void setPostbackAuthHeader(String postbackAuthHeader) {
        this.postbackAuthHeader = postbackAuthHeader;
    }

    public DocumentApiDto getDocument() {
        return document;
    }

    public void setDocument(DocumentApiDto documentApiDto) {
        this.document = documentApiDto;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public Boolean getRequestGeolocation() {
        return requestGeolocation;
    }

    public void setRequestGeolocation(Boolean requestGeolocation) {
        this.requestGeolocation = requestGeolocation;
    }

    public CompanyOverrideApiDto getCompanyOverride() {
        return companyOverride;
    }

    public void setCompanyOverride(CompanyOverrideApiDto companyOverride) {
        this.companyOverride = companyOverride;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(LocalDateTime statusDate) {
        this.statusDate = statusDate;
    }

    public String getModificationCode() {
        return modificationCode;
    }

    public void setModificationCode(String modificationCode) {
        this.modificationCode = modificationCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
