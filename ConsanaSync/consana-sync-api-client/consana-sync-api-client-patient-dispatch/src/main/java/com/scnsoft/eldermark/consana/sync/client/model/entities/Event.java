package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Event")
public class Event extends BaseReadOnlyEntity {

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Resident resident;

    @JoinColumn(name = "event_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private EventType eventType;

    @Column(name = "event_datetime", nullable = false)
    private Instant eventDateTime;

    @Column(name = "assessment")
    private String assessment;

    @Column(name = "situation")
    private String situation;

    @Column(name = "background", columnDefinition = "nvarchar")
    private String background;

    @Column(name = "location", length = 5000, columnDefinition = "nvarchar")
    private String location;

    @Column(name = "is_er_visit", nullable = false)
    private boolean isErVisit;

    @Column(name = "is_injury", nullable = false)
    private boolean isInjury;

    @Column(name = "followup")
    private String followup;

    @Column(name = "is_overnight_in", nullable = false)
    private boolean isOvernightIn;

    @JoinColumn(name = "event_author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private EventAuthor eventAuthor;

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Instant getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Instant eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isErVisit() {
        return isErVisit;
    }

    public void setErVisit(boolean erVisit) {
        isErVisit = erVisit;
    }

    public boolean isInjury() {
        return isInjury;
    }

    public void setInjury(boolean injury) {
        isInjury = injury;
    }

    public String getFollowup() {
        return followup;
    }

    public void setFollowup(String followup) {
        this.followup = followup;
    }

    public boolean isOvernightIn() {
        return isOvernightIn;
    }

    public void setOvernightIn(boolean overnightIn) {
        isOvernightIn = overnightIn;
    }

    public EventAuthor getEventAuthor() {
        return eventAuthor;
    }

    public void setEventAuthor(EventAuthor eventAuthor) {
        this.eventAuthor = eventAuthor;
    }
}
