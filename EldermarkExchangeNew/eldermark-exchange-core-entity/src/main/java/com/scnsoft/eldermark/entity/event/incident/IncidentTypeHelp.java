package com.scnsoft.eldermark.entity.event.incident;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "IncidentTypeHelp")
@Immutable
public class IncidentTypeHelp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "incident_level", nullable = false)
    private Integer incidentLevel;

    @Column(name = "reporting_timelines")
    private String reportingTimelines;

    @Column(name = "followup_requirements")
    private String followupRequirements;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIncidentLevel() {
        return incidentLevel;
    }

    public void setIncidentLevel(Integer incidentLevel) {
        this.incidentLevel = incidentLevel;
    }

    public String getReportingTimelines() {
        return reportingTimelines;
    }

    public void setReportingTimelines(String reportingTimelines) {
        this.reportingTimelines = reportingTimelines;
    }

    public String getFollowupRequirements() {
        return followupRequirements;
    }

    public void setFollowupRequirements(String followupRequirements) {
        this.followupRequirements = followupRequirements;
    }
}
