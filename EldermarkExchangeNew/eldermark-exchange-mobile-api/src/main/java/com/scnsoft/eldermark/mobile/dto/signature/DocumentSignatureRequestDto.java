package com.scnsoft.eldermark.mobile.dto.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;

public class DocumentSignatureRequestDto {

    private Long id;
    private Long expirationDate;

    private String authorFullName;
    private String authorEmail;

    private String templateName;

    private DocumentSignatureStatus status;

    private String pdcFlowLink;
    private String pinCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public DocumentSignatureStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentSignatureStatus status) {
        this.status = status;
    }

    public String getPdcFlowLink() {
        return pdcFlowLink;
    }

    public void setPdcFlowLink(String pdcFlowLink) {
        this.pdcFlowLink = pdcFlowLink;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
