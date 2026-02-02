package com.scnsoft.eldermark.entity.phr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PhrOpenTokSessionDetail")
public class PhrOpenTokSessionDetail extends BaseEntity {

    @Column(name = "opentok_session")
    private String opentokSession = null;

    @Column(name = "opentok_token")
    private String opentokToken = null;

    @Column(name = "session_created_at")
    private Date sessionCreatedAt = null;

    @ManyToOne
    @JoinColumn(name = "session_created_by")
    private User user;

    @Column(name = "is_session_active")
    private Boolean isSessionActive = null;

    public String getOpentokSession() {
        return opentokSession;
    }

    public void setOpentokSession(String opentokSession) {
        this.opentokSession = opentokSession;
    }

    public String getOpentokToken() {
        return opentokToken;
    }

    public void setOpentokToken(String opentokToken) {
        this.opentokToken = opentokToken;
    }

    public Date getSessionCreatedAt() {
        return sessionCreatedAt;
    }

    public void setSessionCreatedAt(Date sessionCreatedAt) {
        this.sessionCreatedAt = sessionCreatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsSessionActive() {
        return isSessionActive;
    }

    public void setIsSessionActive(Boolean isSessionActive) {
        this.isSessionActive = isSessionActive;
    }

}
