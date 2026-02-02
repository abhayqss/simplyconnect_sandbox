package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.Nationalized;

import com.scnsoft.eldermark.entity.incident.IncidentReport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "Event")
@NamedQueries({
        //todo make sure this query is not used anywhere and remove if so
         @NamedQuery(name = "event.getMergedResidents", query = "SELECT DISTINCT r.id, r.firstName, r.lastName, r.birthDate, " +
                "gender.displayName, r.ssnLastFourDigits, (select count(distinct ev.id) from Event ev " +
                "INNER JOIN ev.eventType ev_type INNER JOIN ev.resident ev_resident " +
                "where ev_type.service=false and (ev_resident.id = r.id or ev_resident.id in " +
                "(select merged_surv.survivingCCResident.id from MpiMergedResidents merged_surv " +
                "where merged_surv.mergedCCResident.id = r.id) or ev_resident.id in " +
                "(select merged_merg.mergedCCResident.id from MpiMergedResidents merged_merg " +
                "where merged_merg.survivingCCResident.id = r.id)) ) as ecount, facility.name, r.active, r.dateCreated " +
                "FROM CareCoordinationResident r INNER JOIN r.facility facility LEFT JOIN r.gender gender " +
                "where r.id in (:ids) and r.databaseId = :databaseId")
})
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "event_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private EventType eventType;
    @Lob
    @Basic(optional = false)
    @Column(name = "event_content")
    private String eventContent;
    @Basic(optional = false)
    @Column(name = "event_datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDatetime;
    @Basic(optional = false)
    @Column(name = "is_injury", nullable = false)
    private boolean isInjury;
    @Nationalized
    @Column(name = "location", length = 500)
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
    @Column(name = "followup")
    private String followup;
    @Basic(optional = false)
    @Column(name = "is_manual", nullable = false)
    private boolean isManual;
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private CareCoordinationResident resident;

    @JoinColumn(name = "event_treating_physician_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private EventTreatingPhysician eventTreatingPhysician;
    @JoinColumn(name = "event_treating_hospital_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private EventTreatingHospital eventTreatingHospital;
    @JoinColumn(name = "event_rn_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private EventRN eventRn;
    @JoinColumn(name = "event_manager_id", referencedColumnName = "id")
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    private EventManager eventManager;
    @JoinColumn(name = "event_author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private EventAuthor eventAuthor;

    @Basic(optional = false)
    @Column(name = "is_er_visit", nullable = false)
    private boolean isErVisit;
    @Basic(optional = false)
    @Column(name = "is_overnight_in", nullable = false)
    private boolean isOvernightIn;

    @Column(name = "adt_msg_id")
    private Long adtMsgId;

    @OneToMany(mappedBy = "event")
    private List<Note> notes;
    
    @OneToMany(mappedBy="event")
    private List<IncidentReport> incidentReport;

    String organization;
    String community;

    @Column(name = "auxiliary_info")
    private String auxiliaryInfo;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "person_responsible")
    private String personResponsible;

    @Column(name = "entered_by")
    private String enteredBy;

    @Column(name = "death_indicator")
    private Boolean deathIndicator;

    @Column(name = "death_date")
    private Date deathDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    public Date getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(Date eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    public boolean getIsInjury() {
        return isInjury;
    }

    public void setIsInjury(boolean isInjury) {
        this.isInjury = isInjury;
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

    public boolean getIsFollowup() {
        return isFollowup;
    }

    public void setIsFollowup(boolean isFollowup) {
        this.isFollowup = isFollowup;
    }

    public String getFollowup() {
        return followup;
    }

    public void setFollowup(String followup) {
        this.followup = followup;
    }

    public boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(boolean isManual) {
        this.isManual = isManual;
    }

    public CareCoordinationResident getResident() {
        return resident;
    }

    public void setResident(CareCoordinationResident resident) {
        this.resident = resident;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventTreatingPhysician getEventTreatingPhysician() {
        return eventTreatingPhysician;
    }

    public void setEventTreatingPhysician(EventTreatingPhysician eventTreatingPhysician) {
        this.eventTreatingPhysician = eventTreatingPhysician;
    }

    public EventTreatingHospital getEventTreatingHospital() {
        return eventTreatingHospital;
    }

    public void setEventTreatingHospital(EventTreatingHospital eventTreatingHospital) {
        this.eventTreatingHospital = eventTreatingHospital;
    }

    public EventRN getEventRn() {
        return eventRn;
    }

    public void setEventRn(EventRN eventRn) {
        this.eventRn = eventRn;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public EventAuthor getEventAuthor() {
        return eventAuthor;
    }

    public void setEventAuthor(EventAuthor eventAuthor) {
        this.eventAuthor = eventAuthor;
    }


    public boolean isInjury() {
        return isInjury;
    }

    public boolean isFollowup() {
        return isFollowup;
    }

    public boolean isManual() {
        return isManual;
    }

    public boolean isErVisit() {
        return isErVisit;
    }

    public void setIsErVisit(boolean isErVisit) {
        this.isErVisit = isErVisit;
    }

    public boolean isOvernightIn() {
        return isOvernightIn;
    }

    public void setIsOvernightIn(boolean isOvernightIn) {
        this.isOvernightIn = isOvernightIn;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getAdtMsgId() {
        return adtMsgId;
    }

    public void setAdtMsgId(Long adtMsgId) {
        this.adtMsgId = adtMsgId;
    }

    public String getAuxiliaryInfo() {
        return auxiliaryInfo;
    }

    public void setAuxiliaryInfo(String auxiliaryInfo) {
        this.auxiliaryInfo = auxiliaryInfo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(String personResponsible) {
        this.personResponsible = personResponsible;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<IncidentReport> getIncidentReport() {
        return incidentReport;
    }

    public void setIncidentReport(List<IncidentReport> incidentReport) {
        this.incidentReport = incidentReport;
    }

    /**
     * Event builder
     */
    public static final class Builder {
        String organization;
        String community;
        private Long id;
        private EventType eventType;
        private String eventContent;
        private Date eventDatetime;
        private boolean isInjury;
        private String location;
        private String situation;
        private String background;
        private String assessment;
        private boolean isFollowup;
        private String followup;
        private boolean isManual;
        private CareCoordinationResident resident;
        private EventTreatingPhysician eventTreatingPhysician;
        private EventTreatingHospital eventTreatingHospital;
        private EventRN eventRn;
        private EventManager eventManager;
        private EventAuthor eventAuthor;
        private boolean isErVisit;
        private boolean isOvernightIn;

        private Builder() {
        }

        public static Builder anEvent() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withEventContent(String eventContent) {
            this.eventContent = eventContent;
            return this;
        }

        public Builder withEventDatetime(Date eventDatetime) {
            this.eventDatetime = eventDatetime;
            return this;
        }

        public Builder withIsInjury(boolean isInjury) {
            this.isInjury = isInjury;
            return this;
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withSituation(String situation) {
            this.situation = situation;
            return this;
        }

        public Builder withBackground(String background) {
            this.background = background;
            return this;
        }

        public Builder withAssessment(String assessment) {
            this.assessment = assessment;
            return this;
        }

        public Builder withIsFollowup(boolean isFollowup) {
            this.isFollowup = isFollowup;
            return this;
        }

        public Builder withFollowup(String followup) {
            this.followup = followup;
            return this;
        }

        public Builder withIsManual(boolean isManual) {
            this.isManual = isManual;
            return this;
        }

        public Builder withResident(CareCoordinationResident resident) {
            this.resident = resident;
            return this;
        }

        public Builder withEventTreatingPhysician(EventTreatingPhysician eventTreatingPhysician) {
            this.eventTreatingPhysician = eventTreatingPhysician;
            return this;
        }

        public Builder withEventTreatingHospital(EventTreatingHospital eventTreatingHospital) {
            this.eventTreatingHospital = eventTreatingHospital;
            return this;
        }

        public Builder withEventRn(EventRN eventRn) {
            this.eventRn = eventRn;
            return this;
        }

        public Builder withEventManager(EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        public Builder withEventAuthor(EventAuthor eventAuthor) {
            this.eventAuthor = eventAuthor;
            return this;
        }

        public Builder withIsErVisit(boolean isErVisit) {
            this.isErVisit = isErVisit;
            return this;
        }

        public Builder withIsOvernightIn(boolean isOvernightIn) {
            this.isOvernightIn = isOvernightIn;
            return this;
        }

        public Builder withOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder withCommunity(String community) {
            this.community = community;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.setId(id);
            event.setEventType(eventType);
            event.setEventContent(eventContent);
            event.setEventDatetime(eventDatetime);
            event.setIsInjury(isInjury);
            event.setLocation(location);
            event.setSituation(situation);
            event.setBackground(background);
            event.setAssessment(assessment);
            event.setIsFollowup(isFollowup);
            event.setFollowup(followup);
            event.setIsManual(isManual);
            event.setResident(resident);
            event.setEventTreatingPhysician(eventTreatingPhysician);
            event.setEventTreatingHospital(eventTreatingHospital);
            event.setEventRn(eventRn);
            event.setEventManager(eventManager);
            event.setEventAuthor(eventAuthor);
            event.setIsErVisit(isErVisit);
            event.setIsOvernightIn(isOvernightIn);
            event.setOrganization(organization);
            event.setCommunity(community);
            return event;
        }
    }
}
