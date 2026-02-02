package com.scnsoft.eldermark.dto.signature;

import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.entity.signature.SignatureRequestRecipientType;

public class DocumentSignatureRequestInfoDto {

    private Long id;
    private String statusName;
    private String statusTitle;
    private String authorFullName;
    private String authorEmail;
    private String pdcFlowLink;
    private String pinCode;
    private String templateName;
    private SignatureRequestRecipientType recipientType;
    private SignatureRequestNotificationMethod notificationMethod;
    private Long recipientId;
    private String recipientFullName;
    private String message;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
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

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public SignatureRequestRecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(SignatureRequestRecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public SignatureRequestNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(SignatureRequestNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientFullName() {
        return recipientFullName;
    }

    public void setRecipientFullName(String recipientFullName) {
        this.recipientFullName = recipientFullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
