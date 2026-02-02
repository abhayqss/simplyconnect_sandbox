package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name="companies")
public class PhrChatCompany extends BaseEntity{
    
    @Column(name = "notifyCompanyId")
    private Long notifyCompanyId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "namespace")
    private String namespace ;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "updatedAt")
    private Date updatedAt;
    
    public Long getNotifyCompanyId() {
        return notifyCompanyId;
    }

    public void setNotifyCompanyId(Long notifyCompanyId) {
        this.notifyCompanyId = notifyCompanyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
