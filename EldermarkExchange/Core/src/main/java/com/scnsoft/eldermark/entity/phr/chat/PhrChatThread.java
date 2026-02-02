package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "threads")
public class PhrChatThread extends BaseEntity{
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "quantity")
    private Long quantity;
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "updatedAt")
    private Date updatedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
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
