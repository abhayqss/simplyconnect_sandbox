package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.event.Event;

@Repository
public interface EventJpaDao extends JpaRepository<Event, Long> {
    @Query("select distinct e.adtMsgId from Event e where e.client.id in (:clientIds) and e.adtMsgId is not null")
    List<Long> getAdtMessageIdsForClients(@Param("clientIds") List<Long> clientIds);
}
