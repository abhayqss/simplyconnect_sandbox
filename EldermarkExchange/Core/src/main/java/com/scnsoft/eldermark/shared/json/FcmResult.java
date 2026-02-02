package com.scnsoft.eldermark.shared.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author phomal
 * Created on 8/2/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FcmResult {
    @JsonProperty("error")
    private String error;

    @JsonProperty("message_id")
    private String messageId;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
