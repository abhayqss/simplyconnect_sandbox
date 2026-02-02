package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventJpaDao extends JpaRepository<Event, Long> {
    @Query("select distinct e.adtMsgId from Event e where e.resident.id in (:residentIds) and e.adtMsgId is not null")
    List<Long> getAdtMessageIdsForResidents(@Param("residentIds") List<Long> residentIds);
}
