package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.model.entities.logging.ConsanaEventDispatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsanaEventDispatchLogDao extends JpaRepository<ConsanaEventDispatchLog, Long> {
}
