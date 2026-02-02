package com.scnsoft.eldermark.entity.video;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "EmployeeMissedCallReadStatus")
public class EmployeeMissedCallReadStatus {

    @EmbeddedId
    private EmployeeMissedCallReadStatus.Id id;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Column(name = "twilio_conversation_sid", nullable = false, insertable = false, updatable = false)
    private String twilioConversationSid;

    @Column(name = "last_video_history_read", nullable = false)
    private Instant lastVideoHistoryRead;

    @Column(name = "twilio_identity", nullable = false)
    private String twilioIdentity;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public Instant getLastVideoHistoryRead() {
        return lastVideoHistoryRead;
    }

    public void setLastVideoHistoryRead(Instant lastVideoHistoryRead) {
        this.lastVideoHistoryRead = lastVideoHistoryRead;
    }

    public String getTwilioIdentity() {
        return twilioIdentity;
    }

    public void setTwilioIdentity(String twilioIdentity) {
        this.twilioIdentity = twilioIdentity;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
        private Long employeeId;

        @Column(name = "twilio_conversation_sid", nullable = false, insertable = false, updatable = false)
        private String twilioConversationSid;

        public Long getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(Long employeeId) {
            this.employeeId = employeeId;
        }

        public String getTwilioConversationSid() {
            return twilioConversationSid;
        }

        public void setTwilioConversationSid(String twilioConversationSid) {
            this.twilioConversationSid = twilioConversationSid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(employeeId, id.employeeId) && Objects.equals(twilioConversationSid, id.twilioConversationSid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, twilioConversationSid);
        }
    }
}
