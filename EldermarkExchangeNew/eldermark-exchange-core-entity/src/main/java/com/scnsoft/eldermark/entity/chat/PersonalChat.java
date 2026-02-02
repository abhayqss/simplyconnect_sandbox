package com.scnsoft.eldermark.entity.chat;

import javax.persistence.*;

@Entity
@Table(name = "PersonalChat")
public class PersonalChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "twilio_conversation_sid", nullable = false)
    private String twilioConversationSid;


    @Column(name = "twilio_identity_1", nullable = false)
    private String twilioIdentity1;

    @Column(name = "twilio_identity_2", nullable = false)
    private String twilioIdentity2;

    @Column(name = "client_1_id")
    private Long client1Id;

    @Column(name = "client_2_id")
    private Long client2Id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public String getTwilioIdentity1() {
        return twilioIdentity1;
    }

    public void setTwilioIdentity1(String twilioIdentity1) {
        this.twilioIdentity1 = twilioIdentity1;
    }

    public String getTwilioIdentity2() {
        return twilioIdentity2;
    }

    public void setTwilioIdentity2(String twilioIdentity2) {
        this.twilioIdentity2 = twilioIdentity2;
    }

    public Long getClient1Id() {
        return client1Id;
    }

    public void setClient1Id(Long client1Id) {
        this.client1Id = client1Id;
    }

    public Long getClient2Id() {
        return client2Id;
    }

    public void setClient2Id(Long client2Id) {
        this.client2Id = client2Id;
    }
}
