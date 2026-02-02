package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentReport_IncidentPlaceType_FreeText")
public class IncidentReportIncidentPlaceTypeFreeText implements IncidentReportSetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentReport incidentReport;

    @JoinColumn(name = "incident_place_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentPlaceType incidentPlaceType;

    @JoinColumn(name = "free_text_id", referencedColumnName = "id")
    @ManyToOne(cascade= CascadeType.ALL)
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

    public IncidentPlaceType getIncidentPlaceType() {
        return incidentPlaceType;
    }

    public void setIncidentPlaceType(IncidentPlaceType incidentPlaceType) {
        this.incidentPlaceType = incidentPlaceType;
    }

    public FreeText getFreeText() {
        return freeText;
    }

    public void setFreeText(FreeText freeText) {
        this.freeText = freeText;
    }
}
