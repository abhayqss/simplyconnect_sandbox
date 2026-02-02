package com.scnsoft.eldermark.entity.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "TwilioParticipantReadMessageStatus")
public class TwilioParticipantReadMessageStatus {

    @Id
    @Column(name = "twilio_participant_sid", nullable = false)
    private String twilioParticipantSid;

    @Column(name = "twilio_conversation_sid", nullable = false)
    private String twilioConversationSid;

    @Column(name = "last_read_message_index", nullable = false, columnDefinition = "bigint")
    private Integer lastReadMessageIndex;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    public String getTwilioParticipantSid() {
        return twilioParticipantSid;
    }

    public void setTwilioParticipantSid(String twilioParticipantSid) {
        this.twilioParticipantSid = twilioParticipantSid;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public Integer getLastReadMessageIndex() {
        return lastReadMessageIndex;
    }

    public void setLastReadMessageIndex(Integer lastReadMessageIndex) {
        this.lastReadMessageIndex = lastReadMessageIndex;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
