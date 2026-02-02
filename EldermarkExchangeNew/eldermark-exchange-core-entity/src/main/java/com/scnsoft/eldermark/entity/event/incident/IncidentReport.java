package com.scnsoft.eldermark.entity.event.incident;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.IncidentReportStatus;
import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.event.Event;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Table(name = "IncidentReport")
@Entity
public class IncidentReport extends AuditableEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "rin")
    private String rin;

    @Column(name = "birth_date", columnDefinition = "datetime2")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @Column(name = "unit_number")
    private String unitNumber;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "transition_to_community_date", columnDefinition = "datetime2")
    private LocalDate transitionToCommunityDate;

    @Column(name = "class_member_current_address")
    private String classMemberCurrentAddress;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "agency_address")
    private String agencyAddress;

    @Column(name = "quality_administrator")
    private String qualityAdministrator;

    @Column(name = "care_manager_or_staff_with_prim_serv_resp_and_title")
    private String careManagerOrStaffWithPrimServRespAndTitle;

    @Column(name = "care_manager_or_staff_phone")
    private String careManagerOrStaffPhone;

    @Column(name = "care_manager_or_staff_email")
    private String careManagerOrStaffEmail;

    @Column(name = "mco_care_coordinator_and_agency")
    private String mcoCareCoordinatorAndAgency;

    @Column(name = "mco_care_coordinator_phone")
    private String mcoCareCoordinatorPhone;

    @Column(name = "mco_care_coordinator_email")
    private String mcoCareCoordinatorEmail;

    @Column(name = "incident_datetime")
    private Instant incidentDatetime;

    @Column(name = "incident_discovered_date", columnDefinition = "datetime2")
    private LocalDate incidentDiscoveredDate;

    @Column(name = "was_provider_present_or_scheduled")
    private Boolean wasProviderPresentOrScheduled;

    @Column(name = "was_incident_caused_by_substance")
    private Boolean wasIncidentCausedBySubstance;

    @Column(name = "narrative")
    private String narrative;

    @Column(name = "agency_response_to_incident")
    private String agencyResponseToIncident;

    @Column(name = "report_author")
    private String reportAuthor;

    @Column(name = "report_author_title")
    private String reportAuthorTitle;

    @Column(name = "report_author_phone")
    private String reportAuthorPhone;

    @Deprecated
    @Column(name = "is_submit", nullable = false)
    private boolean submitted;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status")
    private IncidentReportStatus status;

    @Column(name = "report_completed_date")
    private Instant reportCompletedDate;

    @Column(name = "report_date")
    private Instant reportDate;

    @Column(name = "report_by_whom")
    private String reportedBy;

    @Column(name = "report_by_whom_title")
    private String reportedByTitle;

    @Column(name = "report_by_whom_phone")
    private String reportedByPhone;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "class_member_type_id")
    private ClassMemberType classMemberType;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentReportIncidentTypeFreeText> incidentTypes;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypes;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentWeatherConditionTypeFreeText> incidentWeatherConditionTypes;

    @Column(name = "were_other_individuals_involved")
    private Boolean wereOtherIndividualsInvolved;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Individual> individuals;

    @Column(name = "was_incident_participant_taken_to_hospital")
    private Boolean wasIncidentParticipantTakenToHospital;

    @Column(name = "incident_participant_hospital_name")
    private String incidentParticipantHospitalName;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentWitness> witnesses;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentPicture> pictures;

    @Column(name = "were_apparent_injuries")
    private Boolean wereApparentInjuries;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentInjury> incidentInjuries;

    @Column(name = "injured_client_condition")
    private String injuredClientCondition;

    @OneToOne(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private IncidentVitalSigns vitalSigns;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentReportNotification> notifications;

    @Column(name = "immediate_intervention")
    private String immediateIntervention;

    @Column(name = "follow_up_information")
    private String followUpInformation;

    @Column(name = "twilio_conversation_sid")
    private String twilioConversationSid;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getRin() {
        return rin;
    }

    public void setRin(String rin) {
        this.rin = rin;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public LocalDate getTransitionToCommunityDate() {
        return transitionToCommunityDate;
    }

    public void setTransitionToCommunityDate(LocalDate transitionToCommunityDate) {
        this.transitionToCommunityDate = transitionToCommunityDate;
    }

    public String getClassMemberCurrentAddress() {
        return classMemberCurrentAddress;
    }

    public void setClassMemberCurrentAddress(String classMemberCurrentAddress) {
        this.classMemberCurrentAddress = classMemberCurrentAddress;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyAddress() {
        return agencyAddress;
    }

    public void setAgencyAddress(String agencyAddress) {
        this.agencyAddress = agencyAddress;
    }

    public String getQualityAdministrator() {
        return qualityAdministrator;
    }

    public void setQualityAdministrator(String qualityAdministrator) {
        this.qualityAdministrator = qualityAdministrator;
    }

    public String getCareManagerOrStaffWithPrimServRespAndTitle() {
        return careManagerOrStaffWithPrimServRespAndTitle;
    }

    public void setCareManagerOrStaffWithPrimServRespAndTitle(String careManagerOrStaffWithPrimServRespAndTitle) {
        this.careManagerOrStaffWithPrimServRespAndTitle = careManagerOrStaffWithPrimServRespAndTitle;
    }

    public String getCareManagerOrStaffPhone() {
        return careManagerOrStaffPhone;
    }

    public void setCareManagerOrStaffPhone(String careManagerOrStaffPhone) {
        this.careManagerOrStaffPhone = careManagerOrStaffPhone;
    }

    public String getCareManagerOrStaffEmail() {
        return careManagerOrStaffEmail;
    }

    public void setCareManagerOrStaffEmail(String careManagerOrStaffEmail) {
        this.careManagerOrStaffEmail = careManagerOrStaffEmail;
    }

    public String getMcoCareCoordinatorAndAgency() {
        return mcoCareCoordinatorAndAgency;
    }

    public void setMcoCareCoordinatorAndAgency(String mcoCareCoordinatorAndAgency) {
        this.mcoCareCoordinatorAndAgency = mcoCareCoordinatorAndAgency;
    }

    public String getMcoCareCoordinatorPhone() {
        return mcoCareCoordinatorPhone;
    }

    public void setMcoCareCoordinatorPhone(String mcoCareCoordinatorPhone) {
        this.mcoCareCoordinatorPhone = mcoCareCoordinatorPhone;
    }

    public String getMcoCareCoordinatorEmail() {
        return mcoCareCoordinatorEmail;
    }

    public void setMcoCareCoordinatorEmail(String mcoCareCoordinatorEmail) {
        this.mcoCareCoordinatorEmail = mcoCareCoordinatorEmail;
    }

    public Instant getIncidentDatetime() {
        return incidentDatetime;
    }

    public void setIncidentDatetime(Instant incidentDatetime) {
        this.incidentDatetime = incidentDatetime;
    }

    public LocalDate getIncidentDiscoveredDate() {
        return incidentDiscoveredDate;
    }

    public void setIncidentDiscoveredDate(LocalDate incidentDiscoveredDate) {
        this.incidentDiscoveredDate = incidentDiscoveredDate;
    }

    public Boolean getWasProviderPresentOrScheduled() {
        return wasProviderPresentOrScheduled;
    }

    public void setWasProviderPresentOrScheduled(Boolean wasProviderPresentOrScheduled) {
        this.wasProviderPresentOrScheduled = wasProviderPresentOrScheduled;
    }

    public Boolean getWasIncidentCausedBySubstance() {
        return wasIncidentCausedBySubstance;
    }

    public void setWasIncidentCausedBySubstance(Boolean wasIncidentCausedBySubstance) {
        this.wasIncidentCausedBySubstance = wasIncidentCausedBySubstance;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getAgencyResponseToIncident() {
        return agencyResponseToIncident;
    }

    public void setAgencyResponseToIncident(String agencyResponseToIncident) {
        this.agencyResponseToIncident = agencyResponseToIncident;
    }

    public String getReportAuthor() {
        return reportAuthor;
    }

    public void setReportAuthor(String reportAuthor) {
        this.reportAuthor = reportAuthor;
    }

    public String getReportAuthorTitle() {
        return reportAuthorTitle;
    }

    public void setReportAuthorTitle(String reportAuthorTitle) {
        this.reportAuthorTitle = reportAuthorTitle;
    }

    public String getReportAuthorPhone() {
        return reportAuthorPhone;
    }

    public void setReportAuthorPhone(String reportAuthorPhone) {
        this.reportAuthorPhone = reportAuthorPhone;
    }

    public Instant getReportCompletedDate() {
        return reportCompletedDate;
    }

    public void setReportCompletedDate(Instant reportCompletedDate) {
        this.reportCompletedDate = reportCompletedDate;
    }

    public Instant getReportDate() {
        return reportDate;
    }

    public void setReportDate(Instant reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportedByTitle() {
        return reportedByTitle;
    }

    public void setReportedByTitle(String reportedByTitle) {
        this.reportedByTitle = reportedByTitle;
    }

    public String getReportedByPhone() {
        return reportedByPhone;
    }

    public void setReportedByPhone(String reportedByPhone) {
        this.reportedByPhone = reportedByPhone;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Deprecated
    public boolean getSubmitted() {
        return submitted;
    }

    @Deprecated
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public IncidentReportStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentReportStatus status) {
        this.status = status;
    }

    public List<IncidentReportIncidentTypeFreeText> getIncidentTypes() {
        return incidentTypes;
    }

    public void setIncidentTypes(List<IncidentReportIncidentTypeFreeText> incidentTypes) {
        this.incidentTypes = incidentTypes;
    }

    public ClassMemberType getClassMemberType() {
        return classMemberType;
    }

    public void setClassMemberType(ClassMemberType classMemberType) {
        this.classMemberType = classMemberType;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public List<IncidentReportIncidentPlaceTypeFreeText> getIncidentPlaceTypes() {
        return incidentPlaceTypes;
    }

    public void setIncidentPlaceTypes(List<IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypes) {
        this.incidentPlaceTypes = incidentPlaceTypes;
    }

    public List<IncidentWeatherConditionTypeFreeText> getIncidentWeatherConditionTypes() {
        return incidentWeatherConditionTypes;
    }

    public void setIncidentWeatherConditionTypes(List<IncidentWeatherConditionTypeFreeText> incidentWeatherConditionTypes) {
        this.incidentWeatherConditionTypes = incidentWeatherConditionTypes;
    }

    public Boolean getWereOtherIndividualsInvolved() {
        return wereOtherIndividualsInvolved;
    }

    public void setWereOtherIndividualsInvolved(Boolean wereOtherIndividualsInvolved) {
        this.wereOtherIndividualsInvolved = wereOtherIndividualsInvolved;
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public Boolean getWasIncidentParticipantTakenToHospital() {
        return wasIncidentParticipantTakenToHospital;
    }

    public void setWasIncidentParticipantTakenToHospital(Boolean wasIncidentParticipantTakenToHospital) {
        this.wasIncidentParticipantTakenToHospital = wasIncidentParticipantTakenToHospital;
    }

    public String getIncidentParticipantHospitalName() {
        return incidentParticipantHospitalName;
    }

    public void setIncidentParticipantHospitalName(String incidentParticipantHospitalName) {
        this.incidentParticipantHospitalName = incidentParticipantHospitalName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<IncidentWitness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<IncidentWitness> witnesses) {
        this.witnesses = witnesses;
    }

    public List<IncidentPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<IncidentPicture> pictures) {
        this.pictures = pictures;
    }

    public Boolean getWereApparentInjuries() {
        return wereApparentInjuries;
    }

    public void setWereApparentInjuries(Boolean wereApparentInjuries) {
        this.wereApparentInjuries = wereApparentInjuries;
    }

    public List<IncidentInjury> getIncidentInjuries() {
        return incidentInjuries;
    }

    public void setIncidentInjuries(List<IncidentInjury> incidentInjuries) {
        this.incidentInjuries = incidentInjuries;
    }

    public String getInjuredClientCondition() {
        return injuredClientCondition;
    }

    public void setInjuredClientCondition(String injuredClientCondition) {
        this.injuredClientCondition = injuredClientCondition;
    }

    public IncidentVitalSigns getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(IncidentVitalSigns vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public List<IncidentReportNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<IncidentReportNotification> notifications) {
        this.notifications = notifications;
    }

    public String getImmediateIntervention() {
        return immediateIntervention;
    }

    public void setImmediateIntervention(String immediateIntervention) {
        this.immediateIntervention = immediateIntervention;
    }

    public String getFollowUpInformation() {
        return followUpInformation;
    }

    public void setFollowUpInformation(String followUpInformation) {
        this.followUpInformation = followUpInformation;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }
}
