package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.careteam.CareHistory;

@Repository
public interface CareHistoryDao extends JpaRepository<CareHistory, Long> {

    @Query("select p from CareHistory p join p.client res where res.id = :clientId and p.endDate is null order by p.startDate desc")
    List<CareHistory> listByClientId(@Param("clientId") Long clientId);

    @Query("select p from CareHistory p join p.client res where res.id IN :clientIdList and p.endDate is null order by p.startDate desc ")
    List<CareHistory> listByClientIds(@Param("clientIdList") List<Long> clientIdList, Pageable pageable);

}
