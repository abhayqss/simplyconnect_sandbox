package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "SupportTicketReceiverConfiguration")
public class SupportTicketReceiverConfiguration {

    @Id
    @Column(name = "receiver_email")
    private String receiverEmail;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }
}
