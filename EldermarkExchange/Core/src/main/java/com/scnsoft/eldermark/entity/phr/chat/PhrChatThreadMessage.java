package com.scnsoft.eldermark.entity.phr.chat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "thread_messages")
public class PhrChatThreadMessage extends BaseEntity {
 
    @ManyToOne
    @JoinColumn(name = "sender")
    private PhrChatUser phrChatUser;
    
    @ManyToOne
    @JoinColumn(name = "receiver")
    private PhrChatThread phrChatThread;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "text")
    private Byte[] text; 
    
    @Column(name = "createdAt")
    private Date createdAt;
    
    @Column(name = "notifiedAt")
    private Date notifiedAt;
    
    @Column(name = "deliveredAt")
    private Date deliveredAt;

    public PhrChatUser getPhrChatUser() {
        return phrChatUser;
    }

    public void setPhrChatUser(PhrChatUser phrChatUser) {
        this.phrChatUser = phrChatUser;
    }

    public PhrChatThread getPhrChatThread() {
        return phrChatThread;
    }

    public void setPhrChatThread(PhrChatThread phrChatThread) {
        this.phrChatThread = phrChatThread;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Byte[] getText() {
        return text;
    }

    public void setText(Byte[] text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(Date notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }    
}
