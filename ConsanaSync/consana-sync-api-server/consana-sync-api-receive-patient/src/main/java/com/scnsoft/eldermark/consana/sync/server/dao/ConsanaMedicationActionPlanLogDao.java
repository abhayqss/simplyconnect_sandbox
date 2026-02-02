package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.log.ConsanaMedicationActionPlanLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsanaMedicationActionPlanLogDao extends JpaRepository<ConsanaMedicationActionPlanLog, Long> {
}
