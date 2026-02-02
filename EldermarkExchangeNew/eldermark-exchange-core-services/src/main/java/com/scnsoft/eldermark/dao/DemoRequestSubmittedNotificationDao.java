package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.DemoRequestSubmittedNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Instant;

@Repository
public interface DemoRequestSubmittedNotificationDao extends JpaRepository<DemoRequestSubmittedNotification, Long> {

    @Query("update DemoRequestSubmittedNotification n set n.sentDate = :sentDate where n.id = :id")
    @Modifying
    @Transactional
    void updateSentDateById(@Param("id") Long id, @Param("sentDate") Instant sentDate);
}
