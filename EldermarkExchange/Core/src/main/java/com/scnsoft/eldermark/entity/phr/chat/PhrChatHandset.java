package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "handsets")
public class PhrChatHandset extends BaseEntity{
    
    @Column(name = "uuid")
    private String uuid = null;
    
    @Column(name = "pn_token")
    private String pnToken = null;
    
    @Column(name = "type")
    private String type = null;
    
    @Column(name = "device_name")
    private String deviceName = null;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private PhrChatCompany company = null;
    
    @Column(name = "createdAt")
    private Date createdAt = null;
    
    @Column(name = "updatedAt")
    private Date updatedAt = null;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }    

    public String getPnToken() {
        return pnToken;
    }

    public void setPnToken(String pnToken) {
        this.pnToken = pnToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public PhrChatCompany getCompany() {
        return company;
    }

    public void setCompany(PhrChatCompany company) {
        this.company = company;
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
