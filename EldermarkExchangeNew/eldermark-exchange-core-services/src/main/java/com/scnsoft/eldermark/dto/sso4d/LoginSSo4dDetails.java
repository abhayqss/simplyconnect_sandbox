package com.scnsoft.eldermark.dto.sso4d;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginSSo4dDetails {

    @JsonProperty("Employee_ID")
    private String userId;

    @JsonProperty("Still_Logged_In")
    private Boolean stillLoggedIn;

    @JsonProperty("sessionID")
    private String sessionId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getStillLoggedIn() {
        return stillLoggedIn;
    }

    public void setStillLoggedIn(Boolean stillLoggedIn) {
        this.stillLoggedIn = stillLoggedIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
