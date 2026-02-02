package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.event.incident.IncidentReport;

public interface IncidentReportSubmitNotificationService {

    void sendNotifications(IncidentReport incidentReport);
}
