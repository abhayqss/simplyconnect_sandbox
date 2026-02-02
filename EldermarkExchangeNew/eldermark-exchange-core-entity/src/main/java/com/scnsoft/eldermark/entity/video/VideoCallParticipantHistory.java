package com.scnsoft.eldermark.entity.video;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "VideoCallParticipantHistory")
public class VideoCallParticipantHistory implements IdAware {

    public VideoCallParticipantHistory() {
    }

    public VideoCallParticipantHistory(VideoCallHistory videoCallHistory,
                                       String twilioRoomParticipantSid,
                                       String twilioIdentity,
                                       Long employeeRoleId,
                                       VideoCallParticipantState state,
                                       Instant stateDatetime) {
        this.videoCallHistory = videoCallHistory;
        videoCallHistory.getParticipantsHistory().add(this);

        this.twilioRoomParticipantSid = twilioRoomParticipantSid;
        this.twilioIdentity = twilioIdentity;
        this.employeeRoleId = employeeRoleId;
        this.state = state;
        this.stateDatetime = stateDatetime;
    }

    public VideoCallParticipantHistory(VideoCallHistory videoCallHistory,
                                       String twilioRoomParticipantSid,
                                       String twilioIdentity,
                                       Long employeeRoleId,
                                       VideoCallParticipantState state,
                                       Instant stateDatetime,
                                       String stateCausedByIdentity) {
        this(videoCallHistory, twilioRoomParticipantSid, twilioIdentity, employeeRoleId, state, stateDatetime);
        this.stateCausedByIdentity = stateCausedByIdentity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_call_history_id", nullable = false)
    private VideoCallHistory videoCallHistory;

    @Column(name = "video_call_history_id", insertable = false, updatable = false)
    private Long videoCallHistoryId;

    @Column(name = "twilio_room_participant_sid")
    private String twilioRoomParticipantSid;

    @Column(name = "twilio_identity", nullable = false)
    private String twilioIdentity;

    @Column(name = "employee_role_id")
    private Long employeeRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_role_id", insertable = false, updatable = false)
    private CareTeamRole employeeRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private VideoCallParticipantState state;

    @Column(name = "state_datetime", nullable = false)
    private Instant stateDatetime;

    @Column(name = "state_caused_by_identity")
    private String stateCausedByIdentity;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_end_reason")
    private VideoCallParticipantStateEndReason stateEndReason;

    @Column(name = "state_end_datetime")
    private Instant stateEndDatetime;

    @Column(name = "state_end_caused_by_identity")
    private String stateEndCausedByIdentity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VideoCallHistory getVideoCallHistory() {
        return videoCallHistory;
    }

    public void setVideoCallHistory(VideoCallHistory videoCallHistory) {
        this.videoCallHistory = videoCallHistory;
    }

    public Long getVideoCallHistoryId() {
        return videoCallHistoryId;
    }

    public void setVideoCallHistoryId(Long videoCallHistoryId) {
        this.videoCallHistoryId = videoCallHistoryId;
    }

    public String getTwilioRoomParticipantSid() {
        return twilioRoomParticipantSid;
    }

    public void setTwilioRoomParticipantSid(String twilioRoomParticipantSid) {
        this.twilioRoomParticipantSid = twilioRoomParticipantSid;
    }

    public String getTwilioIdentity() {
        return twilioIdentity;
    }

    public void setTwilioIdentity(String twilioIdentity) {
        this.twilioIdentity = twilioIdentity;
    }

    public Long getEmployeeRoleId() {
        return employeeRoleId;
    }

    public void setEmployeeRoleId(Long employeeRoleId) {
        this.employeeRoleId = employeeRoleId;
    }

    public CareTeamRole getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(CareTeamRole employeeRole) {
        this.employeeRole = employeeRole;
    }

    public VideoCallParticipantState getState() {
        return state;
    }

    public void setState(VideoCallParticipantState state) {
        this.state = state;
    }

    public Instant getStateDatetime() {
        return stateDatetime;
    }

    public void setStateDatetime(Instant stateDatetime) {
        this.stateDatetime = stateDatetime;
    }

    public String getStateCausedByIdentity() {
        return stateCausedByIdentity;
    }

    public void setStateCausedByIdentity(String stateCausedByIdentity) {
        this.stateCausedByIdentity = stateCausedByIdentity;
    }

    public VideoCallParticipantStateEndReason getStateEndReason() {
        return stateEndReason;
    }

    public void setStateEndReason(VideoCallParticipantStateEndReason stateEndReason) {
        this.stateEndReason = stateEndReason;
    }

    public Instant getStateEndDatetime() {
        return stateEndDatetime;
    }

    public void setStateEndDatetime(Instant stateEndDatetime) {
        this.stateEndDatetime = stateEndDatetime;
    }

    public String getStateEndCausedByIdentity() {
        return stateEndCausedByIdentity;
    }

    public void setStateEndCausedByIdentity(String stateEndCausedByIdentity) {
        this.stateEndCausedByIdentity = stateEndCausedByIdentity;
    }
}
