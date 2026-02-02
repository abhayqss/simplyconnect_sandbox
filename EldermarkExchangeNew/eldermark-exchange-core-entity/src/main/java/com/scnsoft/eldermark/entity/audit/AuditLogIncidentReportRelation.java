package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.event.incident.IncidentReport;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_IncidentReport")
public class AuditLogIncidentReportRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private IncidentReport incidentReport;

    @Column(name = "incident_report_id", nullable = false)
    private Long incidentReportId;

    public IncidentReport getIncidentReport() {
        return incidentReport;
    }

    public void setIncidentReport(IncidentReport incidentReport) {
        this.incidentReport = incidentReport;
    }

    public Long getIncidentReportId() {
        return incidentReportId;
    }

    public void setIncidentReportId(Long incidentReportId) {
        this.incidentReportId = incidentReportId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(incidentReportId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.INCIDENT_REPORT;
    }
}
