package com.scnsoft.eldermark.dump.entity;


import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "event_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private EventType eventType;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Client client;

    @Column(name = "event_datetime", nullable = false)
    private Instant eventDateTime;

    @Nationalized
    @Column(name = "location", length = 5000)
    private String location;

    @Lob
    @Column(name = "situation")
    private String situation;

    @Column(name = "background")
    private String background;

    @Column(name = "assessment")
    private String assessment;

    @Basic(optional = false)
    @Column(name = "is_followup", nullable = false)
    private boolean isFollowup;

    @Basic(optional = false)
    @Column(name = "is_injury", nullable = false)
    private boolean isInjury;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Instant getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Instant eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public boolean isFollowup() {
        return isFollowup;
    }

    public void setFollowup(boolean followup) {
        isFollowup = followup;
    }

    public boolean isInjury() {
        return isInjury;
    }

    public void setInjury(boolean injury) {
        isInjury = injury;
    }
}
