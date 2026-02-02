package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;

import java.time.Instant;

public class SubmitTemplateSignatureRequest {

    private DocumentSignatureTemplateContext templateContext;

    private Client clientRecipient;
    private Employee employeeRecipient;

    private Instant expirationDate;

    private SignatureRequestNotificationMethod notificationMethod;
    private String phone;
    private String email;
    private String message;

    private Employee requestedBy;

    private DocumentSignatureBulkRequest bulkRequest;

    public DocumentSignatureTemplateContext getTemplateContext() {
        return templateContext;
    }

    public void setTemplateContext(DocumentSignatureTemplateContext templateContext) {
        this.templateContext = templateContext;
    }

    public Client getClientRecipient() {
        return clientRecipient;
    }

    public void setClientRecipient(Client clientRecipient) {
        this.clientRecipient = clientRecipient;
    }

    public Employee getEmployeeRecipient() {
        return employeeRecipient;
    }

    public void setEmployeeRecipient(Employee employeeRecipient) {
        this.employeeRecipient = employeeRecipient;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public SignatureRequestNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(SignatureRequestNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Employee getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Employee requestedBy) {
        this.requestedBy = requestedBy;
    }

    public DocumentSignatureBulkRequest getBulkRequest() {
        return bulkRequest;
    }

    public void setBulkRequest(DocumentSignatureBulkRequest bulkRequest) {
        this.bulkRequest = bulkRequest;
    }
}
