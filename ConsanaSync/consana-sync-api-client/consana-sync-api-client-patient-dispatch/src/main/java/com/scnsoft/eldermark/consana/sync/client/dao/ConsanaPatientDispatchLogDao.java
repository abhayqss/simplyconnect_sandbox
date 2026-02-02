package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.model.entities.logging.ConsanaPatientDispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsanaPatientDispatchLogDao extends JpaRepository<ConsanaPatientDispatchLog, Long> {
}
