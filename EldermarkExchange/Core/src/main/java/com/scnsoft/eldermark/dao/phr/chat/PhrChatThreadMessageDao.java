package com.scnsoft.eldermark.dao.phr.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.chat.PhrChatThreadMessage;

@Repository
public interface PhrChatThreadMessageDao extends JpaRepository<PhrChatThreadMessage, Long>{
    
}
