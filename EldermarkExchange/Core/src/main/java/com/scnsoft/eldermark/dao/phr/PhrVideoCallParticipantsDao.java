package com.scnsoft.eldermark.dao.phr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.phr.PhrVideoCallParticipants;

import java.util.Date;
import java.util.List;

@Repository
public interface PhrVideoCallParticipantsDao extends JpaRepository<PhrVideoCallParticipants, Long> {

    @Query("SELECT pv.user.id FROM PhrVideoCallParticipants pv INNER JOIN pv.phrOpenTokSessionDetail p WHERE p.opentokSession = :sessionId AND p.isSessionActive = true AND pv.user.id <> :userId AND pv.isUserActive = true")
    List<Long> getActiveUserOtherIdsFromSession(@Param("userId") Long userId, @Param("sessionId") String sessionId);

    @Query("SELECT pv.user.id FROM PhrVideoCallParticipants pv INNER JOIN pv.phrOpenTokSessionDetail p WHERE p.opentokSession = :sessionId AND p.isSessionActive = true AND pv.isUserActive = true")
    List<Long> getAllActiveUserIdsFromSession(@Param("sessionId") String sessionId);

    @Query("SELECT pv FROM PhrVideoCallParticipants pv INNER JOIN pv.phrOpenTokSessionDetail p WHERE p.opentokSession = :sessionId AND p.isSessionActive = true AND pv.user.id in (:userId) AND pv.isUserActive = true")
    PhrVideoCallParticipants getActiveUserFromActiveSession(@Param("userId") Long userId,
            @Param("sessionId") String sessionId);

    @Query("SELECT pv FROM PhrVideoCallParticipants pv INNER JOIN pv.phrOpenTokSessionDetail p WHERE p.opentokSession = :sessionId AND p.isSessionActive = true AND pv.isUserActive = true")
    List<PhrVideoCallParticipants> getAllUserFromSession(@Param("sessionId") String sessionId);

    @Query("SELECT pv FROM PhrVideoCallParticipants pv INNER JOIN pv.phrOpenTokSessionDetail p WHERE pv.user.id in (:userId, :receiverId) AND p.user.id in (:userId, :receiverId) AND p.sessionCreatedAt >= :callLogDuration AND p.user.id != pv.user.id ORDER BY p.sessionCreatedAt DESC")
    Page<PhrVideoCallParticipants> getCallLogs(@Param("userId") Long userId, @Param("receiverId") Long receiverId,
            @Param("callLogDuration") Date callLogDuration, Pageable page);

}
