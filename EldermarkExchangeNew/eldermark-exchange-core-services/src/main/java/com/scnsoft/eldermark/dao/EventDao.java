package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.event.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDao extends AppJpaRepository<Event, Long>, CustomEventDao {

    @Query("SELECT DISTINCT e.adtMsgId FROM Event e WHERE e.clientId IN (:clientIds) and e.adtMsgId IS NOT NULL")
    List<Long> getAdtMsgByClientIds(@Param("clientIds") List<Long> clientIds);

}


