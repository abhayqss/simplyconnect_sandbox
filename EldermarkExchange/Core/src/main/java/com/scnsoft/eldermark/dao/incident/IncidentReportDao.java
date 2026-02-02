package com.scnsoft.eldermark.dao.incident;

import com.scnsoft.eldermark.entity.incident.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentReportDao extends JpaRepository<IncidentReport, Long> {
    
    IncidentReport getByEvent_IdAndArchivedIsFalse(Long eventId);

    IncidentReport findFirstByChainIdAndIsSubmitIsTrueOrderById(Long chainId);

}
