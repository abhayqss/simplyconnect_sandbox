package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpentokEntity {
    
    @JsonProperty("openTokSessionId")
    private String sessionId = null;
    
    @JsonProperty("openTokToken")
    private String token = null;
    
    @JsonProperty("openTokApiKey")
    private Integer apiKey = null;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getApiKey() {
        return apiKey;
    }

    public void setApiKey(Integer apiKey) {
        this.apiKey = apiKey;
    }
}
