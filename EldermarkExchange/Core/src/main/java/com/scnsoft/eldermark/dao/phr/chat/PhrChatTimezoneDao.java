package com.scnsoft.eldermark.dao.phr.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.chat.PhrChatTimezone;

@Repository
public interface PhrChatTimezoneDao extends JpaRepository<PhrChatTimezone, Long>{
    
}
