package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.IncidentReportSubmitNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentReportSubmitNotificationDao extends JpaRepository<IncidentReportSubmitNotification, Long> {
}
