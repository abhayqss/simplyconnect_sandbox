package com.scnsoft.eldermark.dao.inbound.document;

import com.scnsoft.eldermark.entity.inbound.document.email.DocumentAssignmentEmailSetting;
import com.scnsoft.eldermark.entity.inbound.document.email.DocumentAssignmentNotificationTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAssignmentEmailSettingsDao extends JpaRepository<DocumentAssignmentEmailSetting, Long> {

    List<DocumentAssignmentEmailSetting> findAllByDatabaseNameAndNotificationTriggerAndDisabledIsFalse(String databaseName,
                                                                                                       DocumentAssignmentNotificationTrigger trigger);

}
