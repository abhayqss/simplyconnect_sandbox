package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.log.ConsanaProblemObservationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsanaProblemObservationLogDao extends JpaRepository<ConsanaProblemObservationLog, Long> {
}
