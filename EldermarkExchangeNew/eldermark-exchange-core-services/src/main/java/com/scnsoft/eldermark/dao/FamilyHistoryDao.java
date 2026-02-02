package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.FamilyHistory;

public interface FamilyHistoryDao extends JpaRepository<FamilyHistory, Long> {
    List<FamilyHistory> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
