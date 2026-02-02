package com.scnsoft.eldermark.web.entity;

import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.phr.OpentokEntity;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "This DTO is intended to represent response to caller")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-02T10:35:33.724+03:00")
public class VideoCallResponseDto {
    @JsonProperty("callType")
    private Call callType = null;

    @JsonProperty("callStartTime")
    private String callStartTime = null;

    @JsonProperty("sessionParticipants")
    private List<UserPersonalDetailsDto> sessionParticipants = null;

    @JsonProperty("callerDetails")
    private UserPersonalDetailsDto callerDetails = null;

    @JsonProperty("openTokData")
    private OpentokEntity opentokData = null;

    public Call getCallType() {
        return callType;
    }

    public void setCallType(Call callType) {
        this.callType = callType;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public List<UserPersonalDetailsDto> getSessionParticipants() {
        return sessionParticipants;
    }

    public void setSessionParticipants(List<UserPersonalDetailsDto> sessionParticipants) {
        this.sessionParticipants = sessionParticipants;
    }

    public UserPersonalDetailsDto getCallerDetails() {
        return callerDetails;
    }

    public void setCallerDetails(UserPersonalDetailsDto callerDetails) {
        this.callerDetails = callerDetails;
    }

    public OpentokEntity getOpentokData() {
        return opentokData;
    }

    public void setOpentokData(OpentokEntity opentokData) {
        this.opentokData = opentokData;
    }

    public enum Call {
        INCOMING, OUTGOING
    }
}
