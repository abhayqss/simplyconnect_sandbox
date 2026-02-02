package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.dao.projections.EventAndReadCount;
import com.scnsoft.eldermark.entity.phr.EventReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 5/17/2017.
 */
@Repository
public interface EventReadStatusDao extends JpaRepository<EventReadStatus, Long> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN 'true' ELSE 'false' END FROM EventReadStatus s WHERE s.eventId = :eventId AND s.userId = :userId")
    boolean existsByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT s.eventId AS eventId, count(s) AS readCount FROM EventReadStatus s WHERE s.eventId IN :eventIds AND s.userId = :userId GROUP BY s.eventId")
    List<EventAndReadCount> getCountByUserIdAndEventIds(@Param("userId") Long userId, @Param("eventIds") Collection<Long> eventIds);

}
