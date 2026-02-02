package com.scnsoft.eldermark.entity.phr.chat;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.phr.BaseEntity;

@Entity
@Table(name = "thread_participant")
public class PhrChatThreadParticipant extends BaseEntity{
    
    @ManyToOne
    @JoinColumn(name = "thread_id")
    private PhrChatThread phrChatThread;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private PhrChatUser phrChatUser;

    public PhrChatThread getPhrChatThread() {
        return phrChatThread;
    }

    public void setPhrChatThread(PhrChatThread phrChatThread) {
        this.phrChatThread = phrChatThread;
    }

    public PhrChatUser getPhrChatUser() {
        return phrChatUser;
    }

    public void setPhrChatUser(PhrChatUser phrChatUser) {
        this.phrChatUser = phrChatUser;
    }
}
