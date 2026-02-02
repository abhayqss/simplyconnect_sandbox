package com.scnsoft.eldermark.dao.phr.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.chat.PhrChatCompany;


@Repository
public interface PhrChatCompanyDao extends JpaRepository<PhrChatCompany, Long> {
    
    PhrChatCompany findByNotifyCompanyId(Long notifyCompanyId);
    
}
