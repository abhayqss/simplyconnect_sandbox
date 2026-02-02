package com.scnsoft.eldermark.dao.phr.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.chat.PhrChatUser;

@Repository
public interface PhrChatUserDao extends JpaRepository<PhrChatUser, Long> {
    
    PhrChatUser findByNotifyUserId(Long notifyUserId);
    
    List<Long> findIdByNotifyUserIdIn(List<Long> notifyUserIds);
}
