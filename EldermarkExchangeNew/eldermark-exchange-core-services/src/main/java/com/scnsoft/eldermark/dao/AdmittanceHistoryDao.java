package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
@Repository
public interface AdmittanceHistoryDao extends JpaRepository<AdmittanceHistory, Long> {

    @Query("select a from AdmittanceHistory a join a.client res where res.id = :clientId order by a.id desc")
    List<AdmittanceHistory> listByClientId(@Param("clientId") Long clientId);

    @Query("select a from AdmittanceHistory a join a.client res where res.id in  (:clientIdList) order by a.id desc")
    List<AdmittanceHistory> listByClientIds(@Param("clientIdList") List<Long> clientIdList);

    List<AdmittanceHistory> findByClient_IdAndCommunityId(Long clientId, Long communityId);


    AdmittanceHistory findByClientIdAndAdmitDate(Long clientId, Instant admitDate);

    AdmittanceHistory findByClientIdAndDischargeDate(Long clientId, Instant dischargeDate);

}
