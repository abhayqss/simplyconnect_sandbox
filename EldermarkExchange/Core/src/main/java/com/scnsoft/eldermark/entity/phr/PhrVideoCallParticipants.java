package com.scnsoft.eldermark.entity.phr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Entity
@Table(name = "PhrVideoCallParticipants")
public class PhrVideoCallParticipants extends BaseEntity {
    
    public enum CallEvents {
        /**
         * Call accept by callee
         */
        CALL_ACCEPT("CALL_ACCEPT"),
        /**
         * Call ended by caller
         */
        CALL_DISCONNECT("CALL_DISCONNECT"),
        /**
         * Call reject by callee
         */
        CALL_DECLINE("CALL_DECLINE"),
        /**
         * callee no response
         */
        CALL_NO_ANSWER("CALL_NO_ANSWER"),
        /**
         * callee on mute
         */
        CALL_ON_MUTE("CALL_ON_MUTE")
        ;

        private final String value;

        CallEvents(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static CallEvents fromValue(String text) {
            text = StringUtils.upperCase(text);
            for (CallEvents b : CallEvents.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
    
    @ManyToOne
    @JoinColumn(name = "video_session_id")
    private PhrOpenTokSessionDetail phrOpenTokSessionDetail;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "is_user_active")
    private Boolean isUserActive = null;
    
    @Column(name = "call_start_time")
    private Date callStartTime = null;
    
    @Column(name = "call_duration")
    private Long callDuration = null;

    @Column(name = "event")
    private String event = null;
    
    @Column(name = "call_type")
    private String callType = null;

    public PhrOpenTokSessionDetail getPhrOpenTokSessionDetail() {
        return phrOpenTokSessionDetail;
    }

    public void setPhrOpenTokSessionDetail(PhrOpenTokSessionDetail phrOpenTokSessionDetail) {
        this.phrOpenTokSessionDetail = phrOpenTokSessionDetail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsUserActive() {
        return isUserActive;
    }

    public void setIsUserActive(Boolean isUserActive) {
        this.isUserActive = isUserActive;
    }

    public Date getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(Date callStartTime) {
        this.callStartTime = callStartTime;
    }

    public Long getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Long callDuration) {
        this.callDuration = callDuration;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
    
}
