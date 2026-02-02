package com.scnsoft.eldermark.dao.inbound.marco;

import com.scnsoft.eldermark.entity.inbound.marco.email.MarcoEmailSettings;
import com.scnsoft.eldermark.entity.inbound.marco.email.MarcoEmailNotificationTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcoEmailSettingsDao extends JpaRepository<MarcoEmailSettings, Long> {

    List<MarcoEmailSettings> findAllByDatabaseNameAndNotificationTrigger(String databaseName, MarcoEmailNotificationTrigger trigger);

}
