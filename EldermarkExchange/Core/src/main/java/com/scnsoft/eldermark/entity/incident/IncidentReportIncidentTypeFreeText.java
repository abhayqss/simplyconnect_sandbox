package com.scnsoft.eldermark.entity.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentReport_IncidentType_FreeText")
public class IncidentReportIncidentTypeFreeText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentReport incidentReport;

    @JoinColumn(name = "incident_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentType incidentType;

    @JoinColumn(name = "free_text_id", referencedColumnName = "id")
    @ManyToOne(cascade=CascadeType.ALL)
    private FreeText freeText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IncidentReport getIncidentReport() {
        return incidentReport;
    }

    public void setIncidentReport(IncidentReport incidentReport) {
        this.incidentReport = incidentReport;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(IncidentType incidentType) {
        this.incidentType = incidentType;
    }

    public FreeText getFreeText() {
        return freeText;
    }

    public void setFreeText(FreeText freeText) {
        this.freeText = freeText;
    }
}
