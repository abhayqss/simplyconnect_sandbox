package com.scnsoft.eldermark.entity.video;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VideoCallHistory")
public class VideoCallHistory {

    public VideoCallHistory() {
    }

    public VideoCallHistory(String callerTwilioIdentity, Instant recordDatetime) {
        this.callerTwilioIdentity = callerTwilioIdentity;
        this.recordDatetime = recordDatetime;
        this.participantsHistory = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "caller_twilio_identity", nullable = false)
    private String callerTwilioIdentity;

    @Column(name = "room_sid")
    private String roomSid;

    @Column(name = "initial_conversation_sid", nullable = false)
    private String initialConversationSid;

    @Column(name = "updated_conversation_sid")
    private String updatedConversationSid;

    @Column(name = "friendly_conversation_name")
    private String friendlyConversationName;

    @Column(name = "record_datetime", nullable = false)
    private Instant recordDatetime;

    @Column(name = "start_datetime")
    private Instant startDatetime;

    @Column(name = "end_datetime")
    private Instant endDatetime;

    @OneToMany(mappedBy = "videoCallHistory", cascade = CascadeType.ALL)
    private List<VideoCallParticipantHistory> participantsHistory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallerTwilioIdentity() {
        return callerTwilioIdentity;
    }

    public void setCallerTwilioIdentity(String callerTwilioIdentity) {
        this.callerTwilioIdentity = callerTwilioIdentity;
    }

    public String getRoomSid() {
        return roomSid;
    }

    public void setRoomSid(String roomSid) {
        this.roomSid = roomSid;
    }

    public String getInitialConversationSid() {
        return initialConversationSid;
    }

    public void setInitialConversationSid(String initialConversationSid) {
        this.initialConversationSid = initialConversationSid;
    }

    public String getUpdatedConversationSid() {
        return updatedConversationSid;
    }

    public void setUpdatedConversationSid(String updatedConversationSid) {
        this.updatedConversationSid = updatedConversationSid;
    }

    public String getFriendlyConversationName() {
        return friendlyConversationName;
    }

    public void setFriendlyConversationName(String friendlyConversationName) {
        this.friendlyConversationName = friendlyConversationName;
    }

    public Instant getRecordDatetime() {
        return recordDatetime;
    }

    public void setRecordDatetime(Instant recordDatetime) {
        this.recordDatetime = recordDatetime;
    }

    public Instant getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Instant startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Instant getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Instant endDatetime) {
        this.endDatetime = endDatetime;
    }

    public List<VideoCallParticipantHistory> getParticipantsHistory() {
        return participantsHistory;
    }

    public void setParticipantsHistory(List<VideoCallParticipantHistory> participantsHistory) {
        this.participantsHistory = participantsHistory;
    }
}
