package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "session_history")
public class PhrChatSessionHistory extends BaseEntity{
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "inTime")
    private Date inTime;
    
    @Column(name = "outTime")
    private Date outTime;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private PhrChatUser phrChatUser;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private PhrChatCompany phrChatCompany;
    
    @ManyToOne
    @JoinColumn(name = "handset_id")
    private PhrChatHandset phrChatHandset;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public PhrChatUser getPhrChatUser() {
        return phrChatUser;
    }

    public void setPhrChatUser(PhrChatUser phrChatUser) {
        this.phrChatUser = phrChatUser;
    }

    public PhrChatCompany getPhrChatCompany() {
        return phrChatCompany;
    }

    public void setPhrChatCompany(PhrChatCompany phrChatCompany) {
        this.phrChatCompany = phrChatCompany;
    }

    public PhrChatHandset getPhrChatHandset() {
        return phrChatHandset;
    }

    public void setPhrChatHandset(PhrChatHandset phrChatHandset) {
        this.phrChatHandset = phrChatHandset;
    }
}
