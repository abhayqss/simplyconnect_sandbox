package com.scnsoft.eldermark.beans.twilio.attributes;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.twilio.chat.SystemMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MessageAttributes {
    private SystemMessage systemMessageName;
    private Boolean isVoiceMessage;
    private Boolean displayLinks;

    private List<MessageReaction> reactions;
    private Map<String, Object> restProps = new HashMap<>();

    public SystemMessage getSystemMessageName() {
        return systemMessageName;
    }

    public MessageAttributes setSystemMessageName(SystemMessage systemMessageName) {
        this.systemMessageName = systemMessageName;
        return this;
    }

    public Boolean getIsVoiceMessage() {
        return isVoiceMessage;
    }

    public void setIsVoiceMessage(Boolean voiceMessage) {
        isVoiceMessage = voiceMessage;
    }

    public Boolean getDisplayLinks() {
        return displayLinks;
    }

    public MessageAttributes setDisplayLinks(Boolean displayLinks) {
        this.displayLinks = displayLinks;
        return this;
    }

    public List<MessageReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<MessageReaction> reactions) {
        this.reactions = reactions;
    }

    @JsonAnyGetter
    public Map<String, Object> restProps() {
        return restProps;
    }

    @JsonAnySetter
    public void setRestProp(String name, Object value) {
        restProps.put(name, value);
    }
}
