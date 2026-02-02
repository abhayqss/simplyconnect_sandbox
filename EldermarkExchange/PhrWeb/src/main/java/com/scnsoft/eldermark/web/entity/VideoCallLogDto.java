package com.scnsoft.eldermark.web.entity;

import java.util.Date;

public class VideoCallLogDto {

    private Long id = null;

    private Long createdBy = null;

    private Long userId = null;

    private Date dateTime = null;

    private String callType = null;

    private String callEvent = null;

    private Long callDuration = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallEvent() {
        return callEvent;
    }

    public void setCallEvent(String callEvent) {
        this.callEvent = callEvent;
    }

    public Long getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(Long callDuration) {
        this.callDuration = callDuration;
    }
}
