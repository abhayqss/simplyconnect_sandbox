package com.scnsoft.eldermark.entity.inbound.marco.email;

import com.scnsoft.eldermark.entity.Database;

import javax.persistence.*;

@Entity
@Table(name = "MarcoEmailSettings")
public class MarcoEmailSettings {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_trigger", nullable = false)
    private MarcoEmailNotificationTrigger notificationTrigger;

    @Column(name = "subject")
    private String subject;

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

    public MarcoEmailNotificationTrigger getNotificationTrigger() {
        return notificationTrigger;
    }

    public void setNotificationTrigger(MarcoEmailNotificationTrigger notificationTrigger) {
        this.notificationTrigger = notificationTrigger;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
