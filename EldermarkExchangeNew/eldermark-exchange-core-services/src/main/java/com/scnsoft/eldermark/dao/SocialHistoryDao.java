package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialHistoryDao extends JpaRepository<SocialHistory, Long> {
    List<SocialHistory> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
