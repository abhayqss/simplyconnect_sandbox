package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.pointclickcare.PccAdtRecordEntity;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

    @Lob
    @Basic
    @Column(name = "event_content")
    private String eventContent;

    @Column(name = "event_datetime", nullable = false)
    private Instant eventDateTime;

    @Basic(optional = false)
    @Column(name = "is_injury", nullable = false)
    private boolean isInjury;

    @Column(name = "location", length = 5000, columnDefinition = "nvarchar")
    @Nationalized
    private String location;

    @Lob
    @Column(name = "situation")
    private String situation;

    @Column(name = "background")
    private String background;

    @Column(name = "assessment")
    private String assessment;

    @OneToOne(mappedBy = "event")
    private ClientAssessmentResult assessmentResult;

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
    private Client client;

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

    private String organization;
    private String community;

    @Column(name = "auxiliary_info")
    private String auxiliaryInfo;

    @Column(name = "device_id")
    private String deviceId;
	
    @OneToMany(mappedBy = "event")
    private List<IncidentReport> incidentReport;

    @Column(name = "event_type_id", nullable = false, insertable = false, updatable = false)
    private Long eventTypeId;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "Event_LabResearchOrder",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "lab_research_order_id")
    )
    private LabResearchOrder labResearchOrder;

    @Column(name = "map_document_id")
    private Long mapDocumentId;

    @OneToOne(mappedBy = "signedEvent")
    private DocumentSignatureRequest documentSignatureRequest;

    @Column(name = "appointment_chain_id")
    private Long appointmentChainId;

    @ManyToOne
    @JoinColumn(name = "pcc_sc_adt_record_id")
    private PccAdtRecordEntity pccAdtRecordEntity;

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

    public Instant getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Instant eventDateTime) {
        this.eventDateTime = eventDateTime;
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

    public ClientAssessmentResult getAssessmentResult() {
        return assessmentResult;
    }

    public void setAssessmentResult(ClientAssessmentResult assessmentResult) {
        this.assessmentResult = assessmentResult;
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

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setInjury(boolean isInjury) {
        this.isInjury = isInjury;
    }

    public void setFollowup(boolean isFollowup) {
        this.isFollowup = isFollowup;
    }

    public void setManual(boolean isManual) {
        this.isManual = isManual;
    }

    public void setErVisit(boolean isErVisit) {
        this.isErVisit = isErVisit;
    }

    public void setOvernightIn(boolean isOvernightIn) {
        this.isOvernightIn = isOvernightIn;
    }

    public EventRN getEventRn() {
        return eventRn;
    }

    public void setEventRn(EventRN eventRn) {
        this.eventRn = eventRn;
	}

    public List<IncidentReport> getIncidentReport() {
        return incidentReport;
    }

    public void setIncidentReport(List<IncidentReport> incidentReport) {
        this.incidentReport = incidentReport;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LabResearchOrder getLabResearchOrder() {
        return labResearchOrder;
    }

    public void setLabResearchOrder(LabResearchOrder labResearchOrder) {
        this.labResearchOrder = labResearchOrder;
    }

    public Long getMapDocumentId() {
        return mapDocumentId;
    }

    public void setMapDocumentId(Long mapDocumentId) {
        this.mapDocumentId = mapDocumentId;
    }

    public DocumentSignatureRequest getDocumentSignatureRequest() {
        return documentSignatureRequest;
    }

    public void setDocumentSignatureRequest(DocumentSignatureRequest documentSignatureRequest) {
        this.documentSignatureRequest = documentSignatureRequest;
    }

    public Long getAppointmentChainId() {
        return appointmentChainId;
    }

    public void setAppointmentChainId(Long appointmentChainId) {
        this.appointmentChainId = appointmentChainId;
    }

    public PccAdtRecordEntity getPccAdtRecordEntity() {
        return pccAdtRecordEntity;
    }

    public void setPccAdtRecordEntity(PccAdtRecordEntity pccAdtRecordEntity) {
        this.pccAdtRecordEntity = pccAdtRecordEntity;
    }
}
