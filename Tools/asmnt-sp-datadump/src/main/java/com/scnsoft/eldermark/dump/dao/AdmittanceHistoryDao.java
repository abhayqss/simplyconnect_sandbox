package com.scnsoft.eldermark.dump.dao;

import com.scnsoft.eldermark.dump.entity.AdmittanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmittanceHistoryDao extends JpaRepository<AdmittanceHistory, Long> {

    List<AdmittanceHistory> findByClient_IdAndCommunityId(Long clientId, Long communityId);

}
