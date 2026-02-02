package com.scnsoft.eldermark.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import org.springframework.data.jpa.repository.query.Procedure;

public interface VitalSignDao extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);

    @Procedure("archive_vital_signs")
    void archive(Instant fromTime, Instant toTime);
}
