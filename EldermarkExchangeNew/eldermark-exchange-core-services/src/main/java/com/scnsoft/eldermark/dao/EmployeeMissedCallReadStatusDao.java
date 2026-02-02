package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.video.EmployeeMissedCallReadStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface EmployeeMissedCallReadStatusDao extends AppJpaRepository<EmployeeMissedCallReadStatus, EmployeeMissedCallReadStatus.Id> {

    @Modifying
    @Query("Update EmployeeMissedCallReadStatus set lastVideoHistoryRead = :when where employeeId = :employeeId")
    void updateLastHistoryReadForEmployee(
            @Param("employeeId") Long employeeId,
            @Param("when") Instant when
    );

    @Modifying
    @Query(nativeQuery = true,
            value = "merge into EmployeeMissedCallReadStatus target " +
                    "using (select distinct ph.twilio_identity, " +
                    "                       isnull(ch.updated_conversation_sid, ch.initial_conversation_sid) twilio_conversation_sid " +
                    "       from VideoCallHistory ch " +
                    "                join VideoCallParticipantHistory ph on ch.id = ph.video_call_history_id " +
                    "       where ch.id = :callHistoryId " +
                    ") source " +
                    "on target.twilio_identity = source.twilio_identity and target.twilio_conversation_sid = source.twilio_conversation_sid " +
                    "when not matched then " +
                    "    insert (employee_id, twilio_conversation_sid, twilio_identity, last_video_history_read) " +
                    "    values ( " +
                    "               convert(bigint, REPLACE(twilio_identity, 'e', '')), " +
                    "               twilio_conversation_sid, " +
                    "               twilio_identity, " +
                    "               :when " +
                    "           ); ")
    void createMissingReadStatuses(
            @Param("callHistoryId") Long callHistoryId,
            @Param("when") Instant when
    );

}
