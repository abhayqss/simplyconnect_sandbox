package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentInjury")
public class IncidentInjury implements IncidentReportSetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

    @ManyToOne(optional = false)
    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    private IncidentReport incidentReport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public IncidentReport getIncidentReport() {
        return incidentReport;
    }

    @Override
    public void setIncidentReport(IncidentReport incidentReport) {
        this.incidentReport = incidentReport;
    }
}
