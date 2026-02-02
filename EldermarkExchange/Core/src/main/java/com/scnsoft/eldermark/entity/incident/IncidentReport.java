package com.scnsoft.eldermark.entity.incident;

import com.scnsoft.eldermark.entity.AuditableEntity;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Event;

import javax.persistence.*;
import java.util.Date;
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
    
    @ManyToOne
    @JoinColumn(name = "class_member_type_id")
    private ClassMemberType classMemberType;
    
    @Column(name = "rin")
    private String rin;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birth_date")
    private Date birthDate;
    
    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;
    
    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transition_to_community_date")
    private Date transitionToCommunityDate;
    
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
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "incident_datetime")
    private Date incidentDatetime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "incident_discovered_date")
    private Date incidentDiscoveredDate;
    
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
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "report_completed_date")
    private Date reportCompletedDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "report_date")
    private Date reportDate;
    
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "is_submit", nullable = false)
    private boolean isSubmit;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Individual> individuals;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypes;

    @OneToMany(mappedBy = "incidentReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentReportIncidentTypeFreeText> incidentTypes;

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

    public ClassMemberType getClassMemberType() {
        return classMemberType;
    }

    public void setClassMemberType(ClassMemberType classMemberType) {
        this.classMemberType = classMemberType;
    }

    public String getRin() {
        return rin;
    }

    public void setRin(String rin) {
        this.rin = rin;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Date getTransitionToCommunityDate() {
        return transitionToCommunityDate;
    }

    public void setTransitionToCommunityDate(Date transitionToCommunityDate) {
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

    public Date getIncidentDatetime() {
        return incidentDatetime;
    }

    public void setIncidentDatetime(Date incidentDatetime) {
        this.incidentDatetime = incidentDatetime;
    }

    public Date getIncidentDiscoveredDate() {
        return incidentDiscoveredDate;
    }

    public void setIncidentDiscoveredDate(Date incidentDiscoveredDate) {
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

    public Date getReportCompletedDate() {
        return reportCompletedDate;
    }

    public void setReportCompletedDate(Date reportCompletedDate) {
        this.reportCompletedDate = reportCompletedDate;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
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

    public boolean getIsSubmit() {
        return isSubmit;
    }

    public void setIsSubmit(boolean submit) {
        isSubmit = submit;
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public List<IncidentReportIncidentPlaceTypeFreeText> getIncidentPlaceTypes() {
        return incidentPlaceTypes;
    }

    public void setIncidentPlaceTypes(List<IncidentReportIncidentPlaceTypeFreeText> incidentPlaceTypes) {
        this.incidentPlaceTypes = incidentPlaceTypes;
    }

    public List<IncidentReportIncidentTypeFreeText> getIncidentTypes() {
        return incidentTypes;
    }

    public void setIncidentTypes(List<IncidentReportIncidentTypeFreeText> incidentTypes) {
        this.incidentTypes = incidentTypes;
    }
}
