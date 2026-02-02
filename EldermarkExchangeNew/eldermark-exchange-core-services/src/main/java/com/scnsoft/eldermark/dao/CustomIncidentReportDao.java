package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Optional;

public interface CustomIncidentReportDao {

    Optional<Instant> findMaxDate(Specification<IncidentReport> specification);

    Optional<Instant> findMinDate(Specification<IncidentReport> specification);
}
