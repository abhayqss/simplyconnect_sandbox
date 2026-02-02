package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "SupportTicketSubmittedNotification")
public class SupportTicketSubmittedNotification {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

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

    public SupportTicket getTicket() {
        return ticket;
    }

    public void setTicket(SupportTicket ticket) {
        this.ticket = ticket;
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
