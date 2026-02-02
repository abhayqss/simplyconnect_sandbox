package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "DemoRequestSubmittedNotification")
public class DemoRequestSubmittedNotification {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demo_request_id")
    private DemoRequest demoRequest;

    @Column(name = "receiver_email")
    private String receiverEmail;
    @Column(name = "created_datetime")
    private Instant createdDate;

    @Column(name = "sent_datetime")
    private Instant sentDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DemoRequest getDemoRequest() {
        return demoRequest;
    }

    public void setDemoRequest(DemoRequest demoRequest) {
        this.demoRequest = demoRequest;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }
}
