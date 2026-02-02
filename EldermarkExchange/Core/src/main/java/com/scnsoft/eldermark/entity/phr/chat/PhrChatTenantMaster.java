package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "tenant_master")
public class PhrChatTenantMaster extends BaseEntity{
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "updatedAt")
    private Date updatedAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
