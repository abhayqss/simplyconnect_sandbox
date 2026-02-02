package com.scnsoft.eldermark.web.entity;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.phr.PhrVideoCallParticipants;
import io.swagger.annotations.ApiModel;

@ApiModel(description = "This DTO is intended to represent Call Event Perform By User")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-02T10:35:33.724+03:00")
public class VideoCallEventDto {
    
    @JsonProperty("eventName")
    private PhrVideoCallParticipants.CallEvents eventName = null;
    
    @JsonProperty("openTokSessionId")
    private String opentokSessionId = null;
    
    @JsonProperty("isSessionContinue")
    private Boolean isSessionContinue = null;
    
    @JsonProperty("isAudioActive")
    private Boolean isAudioActive = true;

    public PhrVideoCallParticipants.CallEvents getEventName() {
        return eventName;
    }

    public void setEventName(PhrVideoCallParticipants.CallEvents eventName) {
        this.eventName = eventName;
    }

    public String getOpentokSessionId() {
        return opentokSessionId;
    }

    public void setOpentokSessionId(String opentokSessionId) {
        this.opentokSessionId = opentokSessionId;
    }

    public Boolean getIsSessionContinue() {
        return isSessionContinue;
    }

    public void setIsSessionContinue(Boolean isSessionContinue) {
        this.isSessionContinue = isSessionContinue;
    }

    public Boolean getIsAudioActive() {
        return isAudioActive;
    }

    public void setIsAudioActive(Boolean isAudioActive) {
        this.isAudioActive = isAudioActive;
    }

    
}
