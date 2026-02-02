package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DocumentSignatureRequestNotification")
public class DocumentSignatureRequestNotification implements Serializable, IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false)
    private SignatureRequestNotificationMethod notificationMethod;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DocumentSignatureRequestNotificationType type;

    @Column(name = "twilio_conversation_sid")
    private String conversationSid;

    @ManyToOne
    @JoinColumn(name = "signature_request_id")
    private DocumentSignatureRequest documentSignatureRequest;

    @Column(name = "signature_request_id", insertable = false, updatable = false)
    private Long documentSignatureRequestId;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

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

    public DocumentSignatureRequestNotificationType getType() {
        return type;
    }

    public void setType(DocumentSignatureRequestNotificationType type) {
        this.type = type;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public DocumentSignatureRequest getDocumentSignatureRequest() {
        return documentSignatureRequest;
    }

    public void setDocumentSignatureRequest(DocumentSignatureRequest documentSignatureRequest) {
        this.documentSignatureRequest = documentSignatureRequest;
    }

    public Long getDocumentSignatureRequestId() {
        return documentSignatureRequestId;
    }

    public void setDocumentSignatureRequestId(Long documentSignatureRequestId) {
        this.documentSignatureRequestId = documentSignatureRequestId;
    }
}
