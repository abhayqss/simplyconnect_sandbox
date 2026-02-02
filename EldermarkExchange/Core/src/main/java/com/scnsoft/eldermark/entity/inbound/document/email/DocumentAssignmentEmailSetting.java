package com.scnsoft.eldermark.entity.inbound.document.email;

import com.scnsoft.eldermark.entity.Database;

import javax.persistence.*;

@Entity
@Table(name = "DocumentAssignmentEmailSetting")
public class DocumentAssignmentEmailSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "notification_trigger", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAssignmentNotificationTrigger notificationTrigger;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "disabled", nullable = false)
    private boolean disabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public DocumentAssignmentNotificationTrigger getNotificationTrigger() {
        return notificationTrigger;
    }

    public void setNotificationTrigger(DocumentAssignmentNotificationTrigger notificationTrigger) {
        this.notificationTrigger = notificationTrigger;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}