package com.scnsoft.eldermark.mobile.dto.document;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;

public class DocumentSignatureDto {

    private Long requestId;

    private DocumentSignatureStatus status;
    private String templateName;

    private Long requestedDate;
    private SignatureRequestNotificationMethod notificationMethod;
    private String email;
    private String phone;
    private Long requestExpirationDate;

    private Long signedDate;
    private String signerIp;
    private String signerLocation;

    private Long canceledDate;
    private Long failedDate;

    private String pdcFlowPinCode;
    private String pdcFlowLink;

    private String errorMessage;

    private Boolean canRenew;
    private Boolean canSign;

    private Long canResendPinAt;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public DocumentSignatureStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentSignatureStatus status) {
        this.status = status;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Long requestedDate) {
        this.requestedDate = requestedDate;
    }

    public SignatureRequestNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(SignatureRequestNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getRequestExpirationDate() {
        return requestExpirationDate;
    }

    public void setRequestExpirationDate(Long requestExpirationDate) {
        this.requestExpirationDate = requestExpirationDate;
    }

    public Long getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Long signedDate) {
        this.signedDate = signedDate;
    }

    public String getSignerIp() {
        return signerIp;
    }

    public void setSignerIp(String signerIp) {
        this.signerIp = signerIp;
    }

    public String getSignerLocation() {
        return signerLocation;
    }

    public void setSignerLocation(String signerLocation) {
        this.signerLocation = signerLocation;
    }

    public Long getCanceledDate() {
        return canceledDate;
    }

    public void setCanceledDate(Long canceledDate) {
        this.canceledDate = canceledDate;
    }

    public Long getFailedDate() {
        return failedDate;
    }

    public void setFailedDate(Long failedDate) {
        this.failedDate = failedDate;
    }

    public String getPdcFlowPinCode() {
        return pdcFlowPinCode;
    }

    public void setPdcFlowPinCode(String pdcFlowPinCode) {
        this.pdcFlowPinCode = pdcFlowPinCode;
    }

    public String getPdcFlowLink() {
        return pdcFlowLink;
    }

    public void setPdcFlowLink(String pdcFlowLink) {
        this.pdcFlowLink = pdcFlowLink;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getCanRenew() {
        return canRenew;
    }

    public void setCanRenew(Boolean canRenew) {
        this.canRenew = canRenew;
    }

    public Boolean getCanSign() {
        return canSign;
    }

    public void setCanSign(Boolean canSign) {
        this.canSign = canSign;
    }

    public Long getCanResendPinAt() {
        return canResendPinAt;
    }

    public void setCanResendPinAt(Long canResendPinAt) {
        this.canResendPinAt = canResendPinAt;
    }
}
