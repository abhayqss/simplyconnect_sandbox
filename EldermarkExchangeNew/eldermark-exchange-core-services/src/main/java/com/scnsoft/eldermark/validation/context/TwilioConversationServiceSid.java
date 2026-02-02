package com.scnsoft.eldermark.validation.context;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TwilioConversationServiceSid")
public class TwilioConversationServiceSid {

    @Id
    @Column(name = "twilio_service_sid", insertable = false, updatable = false)
    private String serviceSid;

    @Column(name = "twilio_account_sid", insertable = false, updatable = false)
    private String accountSid;


    public String getServiceSid() {
        return serviceSid;
    }

    public void setServiceSid(String serviceSid) {
        this.serviceSid = serviceSid;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }
}