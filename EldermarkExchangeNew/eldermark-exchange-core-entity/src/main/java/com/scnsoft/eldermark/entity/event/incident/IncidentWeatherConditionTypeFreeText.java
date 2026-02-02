package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentReport_IncidentWeatherConditionType_FreeText")
public class IncidentWeatherConditionTypeFreeText implements IncidentReportSetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentReport incidentReport;

    @JoinColumn(name = "incident_weather_condition_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private IncidentWeatherConditionType incidentWeatherConditionType;

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

    public IncidentWeatherConditionType getIncidentWeatherConditionType() {
        return incidentWeatherConditionType;
    }

    public void setIncidentWeatherConditionType(IncidentWeatherConditionType incidentWeatherConditionType) {
        this.incidentWeatherConditionType = incidentWeatherConditionType;
    }

    public FreeText getFreeText() {
        return freeText;
    }

    public void setFreeText(FreeText freeText) {
        this.freeText = freeText;
    }
}
