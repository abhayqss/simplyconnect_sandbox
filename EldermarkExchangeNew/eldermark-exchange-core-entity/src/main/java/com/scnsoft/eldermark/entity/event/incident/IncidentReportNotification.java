package com.scnsoft.eldermark.entity.event.incident;

import com.scnsoft.eldermark.entity.IncidentReportNotificationDestination;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "IncidentReportNotification")
public class IncidentReportNotification implements IncidentReportSetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination")
    private IncidentReportNotificationDestination destination;

    @Column(name = "datetime")
    private Instant datetime;

    @Column(name = "by_whom")
    private String byWhom;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "response")
    private String response;

    @Column(name = "response_datetime")
    private Instant responseDatetime;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_notified")
    private Boolean isNotified;

    @ManyToOne(optional = false)
    @JoinColumn(name = "incident_report_id", referencedColumnName = "id", nullable = false)
    private IncidentReport incidentReport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IncidentReportNotificationDestination getDestination() {
        return destination;
    }

    public void setDestination(IncidentReportNotificationDestination destination) {
        this.destination = destination;
    }

    public Instant getDatetime() {
        return datetime;
    }

    public void setDatetime(Instant datetime) {
        this.datetime = datetime;
    }

    public String getByWhom() {
        return byWhom;
    }

    public void setByWhom(String byWhom) {
        this.byWhom = byWhom;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Instant getResponseDatetime() {
        return responseDatetime;
    }

    public void setResponseDatetime(Instant responseDatetime) {
        this.responseDatetime = responseDatetime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    public IncidentReport getIncidentReport() {
        return incidentReport;
    }

    @Override
    public void setIncidentReport(IncidentReport incidentReport) {
        this.incidentReport = incidentReport;
    }
}
