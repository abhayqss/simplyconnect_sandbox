package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.log.ConsanaEncounterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsanaEncounterLogDao extends JpaRepository<ConsanaEncounterLog, Long> {
}
