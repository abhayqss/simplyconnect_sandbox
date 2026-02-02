package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureRequestFromAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.event.Event;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "DocumentSignatureRequest")
public class DocumentSignatureRequest implements DocumentSignatureRequestFromAware, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_template_id")
    private DocumentSignatureTemplate signatureTemplate;

    @Column(name = "signature_template_id", insertable = false, updatable = false)
    private Long signatureTemplateId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_employee_id", nullable = false)
    private Employee requestedBy;

    @Column(name = "requested_by_employee_id", nullable = false, insertable = false, updatable = false)
    private Long requestedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_from_employee_id")
    private Employee requestedFromEmployee;

    @Column(name = "requested_from_employee_id", insertable = false, updatable = false)
    private Long requestedFromEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_from_resident_id")
    private Client requestedFromClient;

    @Column(name = "requested_from_resident_id", insertable = false, updatable = false)
    private Long requestedFromClientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method")
    private SignatureRequestNotificationMethod notificationMethod;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "message")
    private String message;

    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;

    @Column(name = "date_expires", nullable = false)
    private Instant dateExpires;

    @Column(name = "pdcflow_signature_url")
    private String pdcflowSignatureUrl;

    @Column(name = "pdcflow_pin_code")
    private String pdcflowPinCode;

    @Column(name = "pdcflow_signature_id", columnDefinition = "decimal")
    private BigInteger pdcflowSignatureId;

    @Column(name = "pdcflow_error_code")
    private String pdcflowErrorCode;

    @Column(name = "pdcflow_error_message")
    private String pdcflowErrorMessage;

    @Column(name = "pdcflow_error_datetime")
    private Instant pdcflowErrorDatetime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_event_id")
    private Event signedEvent;

    @Column(name = "date_signed")
    private Instant dateSigned;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentSignatureRequestStatus status;

    @Column(name = "date_canceled")
    private Instant dateCanceled;

    @Column(name = "canceled_by_id", insertable = false, updatable = false)
    private Long canceledByEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by_id")
    private Employee canceledByEmployee;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "signatureRequest")
    private Document document;

    @OneToMany(mappedBy = "signatureRequest", cascade = CascadeType.ALL)
    private List<DocumentSignatureRequestSubmittedField> submittedFields;

    @OneToMany(mappedBy = "signatureRequest", cascade = CascadeType.ALL)
    private List<DocumentSignatureRequestNotSubmittedField> notSubmittedFields;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @OneToMany(mappedBy = "documentSignatureRequest")
    private List<DocumentSignatureRequestNotification> notifications;

    @ManyToOne
    @JoinColumn(name = "bulk_request_id")
    private DocumentSignatureBulkRequest bulkRequest;

    @Column(name = "bulk_request_id", insertable = false, updatable = false)
    private Long bulkRequestId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentSignatureTemplate getSignatureTemplate() {
        return signatureTemplate;
    }

    public void setSignatureTemplate(DocumentSignatureTemplate signatureTemplate) {
        this.signatureTemplate = signatureTemplate;
        if (signatureTemplate != null) {
            setSignatureTemplateId(signatureTemplate.getId());
        }
    }

    public Long getSignatureTemplateId() {
        return signatureTemplateId;
    }

    public void setSignatureTemplateId(Long signatureTemplateId) {
        this.signatureTemplateId = signatureTemplateId;
    }

    public Employee getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Employee requestedBy) {
        this.requestedBy = requestedBy;
        if (requestedBy != null) {
            setRequestedById(requestedBy.getId());
        }
    }

    @Override
    public Long getRequestedById() {
        return requestedById;
    }

    public void setRequestedById(Long requestedById) {
        this.requestedById = requestedById;
    }

    public Employee getRequestedFromEmployee() {
        return requestedFromEmployee;
    }

    public void setRequestedFromEmployee(Employee requestedFromEmployee) {
        this.requestedFromEmployee = requestedFromEmployee;
        if (requestedFromEmployee != null) {
            setRequestedFromEmployeeId(requestedFromEmployee.getId());
        }
    }

    @Override
    public Long getRequestedFromEmployeeId() {
        return requestedFromEmployeeId;
    }

    public void setRequestedFromEmployeeId(Long requestedFromEmployeeId) {
        this.requestedFromEmployeeId = requestedFromEmployeeId;
    }

    public Client getRequestedFromClient() {
        return requestedFromClient;
    }

    public void setRequestedFromClient(Client requestedFromClient) {
        this.requestedFromClient = requestedFromClient;
        if (requestedFromClient != null) {
            setRequestedFromClientId(requestedFromClient.getId());
        }
    }

    @Override
    public Long getRequestedFromClientId() {
        return requestedFromClientId;
    }

    public void setRequestedFromClientId(Long requestedFromClientId) {
        this.requestedFromClientId = requestedFromClientId;
    }

    @Override
    public SignatureRequestNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(SignatureRequestNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Instant getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Instant dateExpires) {
        this.dateExpires = dateExpires;
    }

    public String getPdcflowSignatureUrl() {
        return pdcflowSignatureUrl;
    }

    public void setPdcflowSignatureUrl(String pdcflowSignatureUrl) {
        this.pdcflowSignatureUrl = pdcflowSignatureUrl;
    }

    public String getPdcflowPinCode() {
        return pdcflowPinCode;
    }

    public void setPdcflowPinCode(String pdcflowPinCode) {
        this.pdcflowPinCode = pdcflowPinCode;
    }

    public BigInteger getPdcflowSignatureId() {
        return pdcflowSignatureId;
    }

    public void setPdcflowSignatureId(BigInteger pdcflowSignatureId) {
        this.pdcflowSignatureId = pdcflowSignatureId;
    }

    public String getPdcflowErrorCode() {
        return pdcflowErrorCode;
    }

    public void setPdcflowErrorCode(String pdcflowErrorCode) {
        this.pdcflowErrorCode = pdcflowErrorCode;
    }

    public String getPdcflowErrorMessage() {
        return pdcflowErrorMessage;
    }

    public void setPdcflowErrorMessage(String pdcflowErrorMessage) {
        this.pdcflowErrorMessage = pdcflowErrorMessage;
    }

    public Instant getPdcflowErrorDatetime() {
        return pdcflowErrorDatetime;
    }

    public void setPdcflowErrorDatetime(Instant pdcflowErrorDatetime) {
        this.pdcflowErrorDatetime = pdcflowErrorDatetime;
    }

    public Event getSignedEvent() {
        return signedEvent;
    }

    public void setSignedEvent(Event signedEvent) {
        this.signedEvent = signedEvent;
    }

    public Instant getDateSigned() {
        return dateSigned;
    }

    public void setDateSigned(Instant date_signed) {
        this.dateSigned = date_signed;
    }

    public DocumentSignatureRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentSignatureRequestStatus status) {
        this.status = status;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<DocumentSignatureRequestSubmittedField> getSubmittedFields() {
        return submittedFields;
    }

    public void setSubmittedFields(List<DocumentSignatureRequestSubmittedField> submittedFields) {
        this.submittedFields = submittedFields;
    }

    public List<DocumentSignatureRequestNotSubmittedField> getNotSubmittedFields() {
        return notSubmittedFields;
    }

    public void setNotSubmittedFields(List<DocumentSignatureRequestNotSubmittedField> notSubmittedFields) {
        this.notSubmittedFields = notSubmittedFields;
    }

    public Instant getDateCanceled() {
        return dateCanceled;
    }

    public void setDateCanceled(Instant dateCanceled) {
        this.dateCanceled = dateCanceled;
    }

    public Long getCanceledByEmployeeId() {
        return canceledByEmployeeId;
    }

    public void setCanceledByEmployeeId(Long canceledByEmployeeId) {
        this.canceledByEmployeeId = canceledByEmployeeId;
    }

    public Employee getCanceledByEmployee() {
        return canceledByEmployee;
    }

    public void setCanceledByEmployee(Employee canceledByEmployee) {
        this.canceledByEmployee = canceledByEmployee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<DocumentSignatureRequestNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<DocumentSignatureRequestNotification> notifications) {
        this.notifications = notifications;
    }

    public DocumentSignatureBulkRequest getBulkRequest() {
        return bulkRequest;
    }

    public void setBulkRequest(DocumentSignatureBulkRequest bulkRequest) {
        this.bulkRequest = bulkRequest;
    }

    public Long getBulkRequestId() {
        return bulkRequestId;
    }

    public void setBulkRequestId(Long bulkRequestId) {
        this.bulkRequestId = bulkRequestId;
    }
}
