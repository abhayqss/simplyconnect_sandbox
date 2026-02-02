package com.scnsoft.eldermark.dao.video;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VideoCallHistoryDao extends AppJpaRepository<VideoCallHistory, Long> {

    //lock the row so that concurrent webhooks from Twilio are processed correctly one by one
    @Query("Select ch from VideoCallHistory ch where ch.roomSid = :roomSid")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    VideoCallHistory findLockedByRoomSid(@Param("roomSid") String roomSid);

    @Query("Select ch from VideoCallHistory ch where ch.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<VideoCallHistory> findLockedById(@Param("id") Long id);

    @Query("Select ch from VideoCallHistory ch where " +
            "(ch.initialConversationSid = :conversationSid or ch.updatedConversationSid = :conversationSid) and " +
            "ch.endDatetime is null")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<VideoCallHistory> findLockedActiveByConversationSid(@Param("conversationSid") String conversationSid);

    @Modifying
    @Query("update VideoCallHistory set endDatetime = " +
            "CASE when endDatetime is null then :endDatetime else endDatetime END " +
            "where roomSid = :roomSid")
    void writeCallEndTimeIfMissing(@Param("roomSid") String roomSid, @Param("endDatetime") Instant endDatetime);


    List<VideoCallHistory> findAllByEndDatetimeIsNull();

}
