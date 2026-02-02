package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "users")
public class PhrChatUser extends BaseEntity{
    
    @Column(name = "notifyUserId")
    private Long notifyUserId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "logged")
    private Long logged;
    
    @Column(name = "role")
    private String role;    
    
    @Column(name = "current_handset")
    private String currentHandset;
    
    @ManyToOne
    @JoinColumn(name = "timezone_id")
    private PhrChatTimezone phrChatTimezone;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private PhrChatCompany phrChatCompany;
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "updatedAt")
    private Date updatedAt;

    public Long getNotifyUserId() {
        return notifyUserId;
    }

    public void setNotifyUserId(Long notifyUserId) {
        this.notifyUserId = notifyUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLogged() {
        return logged;
    }

    public void setLogged(Long logged) {
        this.logged = logged;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCurrentHandset() {
        return currentHandset;
    }

    public void setCurrentHandset(String currentHandset) {
        this.currentHandset = currentHandset;
    }

    public PhrChatTimezone getPhrChatTimezone() {
        return phrChatTimezone;
    }

    public void setPhrChatTimezone(PhrChatTimezone phrChatTimezone) {
        this.phrChatTimezone = phrChatTimezone;
    }

    public PhrChatCompany getPhrChatCompany() {
        return phrChatCompany;
    }

    public void setPhrChatCompany(PhrChatCompany phrChatCompany) {
        this.phrChatCompany = phrChatCompany;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}
