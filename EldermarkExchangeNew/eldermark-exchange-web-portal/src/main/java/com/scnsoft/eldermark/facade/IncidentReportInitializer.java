package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.entity.Employee;

public interface IncidentReportInitializer {

    IncidentReportDto initIncidentReport(Long eventId, Employee employee);
}
