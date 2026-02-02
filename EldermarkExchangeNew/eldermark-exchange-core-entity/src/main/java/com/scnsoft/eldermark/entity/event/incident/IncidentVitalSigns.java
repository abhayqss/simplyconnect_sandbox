package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentVitalSigns")
public class IncidentVitalSigns implements IncidentReportSetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "pulse")
    private String pulse;

    @Column(name = "respiration_rate")
    private String respirationRate;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "O2_saturation")
    private String o2Saturation;

    @Column(name = "blood_sugar")
    private String bloodSugar;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    private IncidentReport incidentReport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getRespirationRate() {
        return respirationRate;
    }

    public void setRespirationRate(String respirationRate) {
        this.respirationRate = respirationRate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getO2Saturation() {
        return o2Saturation;
    }

    public void setO2Saturation(String o2Saturation) {
        this.o2Saturation = o2Saturation;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public IncidentReport getIncidentReport() {
        return incidentReport;
    }

    @Override
    public void setIncidentReport(IncidentReport incidentReport) {
        this.incidentReport = incidentReport;
    }
}
