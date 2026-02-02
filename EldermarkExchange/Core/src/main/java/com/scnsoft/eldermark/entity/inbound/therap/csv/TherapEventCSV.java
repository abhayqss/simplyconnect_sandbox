package com.scnsoft.eldermark.entity.inbound.therap.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.scnsoft.eldermark.services.converters.csv.BooleanFieldConverter;

import java.util.Calendar;

public class TherapEventCSV {
    private static final String DATE_PATTERN = "MM/dd/yyyy";
    private static final String TIME_PATTERN = "hh:mm a";

    @CsvBindByName(column = "IDFFORMID")
    private String idFormId;

    @CsvBindByName(column = "GERFORMID")
    private String gerFormId;

    @CsvBindByName(column = "ORGANISATIONNAME")
    private String organizationName;

    @CsvBindByName(column = "ORGANISATIONID")
    private String organizationId;

    @CsvBindByName(column = "ORGANISATIONPHONE")
    private String organizationPhone;

    @CsvBindByName(column = "ORGANISATIONEMAIL")
    private String organizationEmail;

    @CsvBindByName(column = "COMMUNITYNAME")
    private String communityName;

    @CsvBindByName(column = "COMMUNITYID")
    private String communityId;

    @CsvBindByName(column = "COMMUNITYEMAIL")
    private String communityEmail;

    @CsvBindByName(column = "COMMUNITYPHONE")
    private String communityPhone;

    @CsvBindByName(column = "SITEID")
    private String siteId;

    @CsvBindByName(column = "SITENAME")
    private String siteName;

    @CsvBindByName(column = "INDIVIDUALFIRSTNAME")
    private String individualFirstname;

    @CsvBindByName(column = "INDIVIDUALLASTNAME")
    private String individualLastname;

    @CsvBindByName(column = "INDIVIDUALIDNUMBER")
    private String individualIdNumber;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "INDIVIDUALDATEOFBIRTH")
    private Calendar individualDateOfBirth;

    @CsvBindByName(column = "INDIVIDUALSSN")
    private String individualSsn;

    @CsvBindByName(column = "INDIVIDUALADDRESSSTREET")
    private String individualAddressStreet;

    @CsvBindByName(column = "INDIVIDUALADDRESSCITY")
    private String individualAddressCity;

    @CsvBindByName(column = "INDIVIDUALADDRESSSTATE")
    private String individualAddressState;

    @CsvBindByName(column = "INDIVIDUALADDRESSZIP")
    private String individualAddressZip;

    @CsvBindByName(column = "INDIVIDUALGENDER")
    private String individualGenger;

    @CsvBindByName(column = "INDIVIDUALMARITALSTATUS")
    private Integer individualMaritalStatus;

    @CsvBindByName(column = "FORMAUTHORFIRSTNAME")
    private String formAuthorFirstName;

    @CsvBindByName(column = "FORMAUTHORLASTNAME")
    private String formAuthorLastName;

    @CsvBindByName(column = "FORMAUTHORROLE")
    private String formAuthorRole;

    @CsvBindByName(column = "FORMAUTHORORGANIZATION")
    private String formAuthorOrganization;

    @CsvBindByName(column = "MANAGERFIRSTNAME")
    private String managerFirstname;

    @CsvBindByName(column = "MANAGERLASTNAME")
    private String managerLastname;

    @CsvBindByName(column = "MANAGERPHONE")
    private String managerPhone;

    @CsvBindByName(column = "MANAGEREMAIL")
    private String managerEmail;

    @CsvBindByName(column = "RNFIRSTNAME")
    private String rnFirstname;

    @CsvBindByName(column = "RNLASTNAME")
    private String rnLastname;

    @CsvBindByName(column = "RNSTREET")
    private String rnStreet;

    @CsvBindByName(column = "RNCITY")
    private String rnCity;

    @CsvBindByName(column = "RNSTATE")
    private String rnState;

    @CsvBindByName(column = "RNZIP")
    private String rnZip;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "EVENTDATE")
    private Calendar eventDate;

    @CsvBindByName(column = "EVENTMONTH")
    private Integer eventMonth;

    @CsvBindByName(column = "EVENTDAY")
    private Integer eventDay;

    @CsvBindByName(column = "EVENTYEAR")
    private Integer eventYear;

    @CsvDate(TIME_PATTERN)
    @CsvBindByName(column = "EVENTTIME")
    private Calendar eventTime;

    @CsvCustomBindByName(column = "HASINJURY", converter = BooleanFieldConverter.class)
    private Boolean hasInjury;

    @CsvBindByName(column = "EVENTLOCATION")
    private String eventLocation;

    @CsvBindByName(column = "EVENTTYPE")
    private Integer eventType;

    @CsvDate(DATE_PATTERN)
    @CsvBindByName(column = "EVENTDEATHDISCOVEREDDATE")
    private Calendar eventDeathDiscoveredDate;

    @CsvDate(TIME_PATTERN)
    @CsvBindByName(column = "EVENTDEATHDISCOVEREDTIME")
    private Calendar eventDeathDiscoveredTime;

    @CsvBindByName(column = "PERSONRESPONSIBLE")
    private String personResponsible;

    @CsvBindByName(column = "OTHEREVENTTYPE")
    private Integer otherEventType;

    @CsvBindByName(column = "OTHEREVENTSUBTYPE")
    private Integer otherEventSubType;

    @CsvBindByName(column = "SITUATIONNARRATIVE")
    private String situationNarrative;

    @CsvBindByName(column = "BACKGROUNDNARRATIVE")
    private String backgroundNarrative;

    @CsvBindByName(column = "ASSESSMENTNARRATIVE")
    private String assessmentNarrative;

    @CsvBindByName(column = "ENTEREDBY")
    private String enteredBy;

    @CsvBindByName(column = "FOLLOWUPDETAILS")
    private String followUpDetails;

    @CsvCustomBindByName(column = "FOLLOWUPISEXPECTED", converter = BooleanFieldConverter.class)
    private Boolean followUpExpected;

    @CsvBindByName(column = "TREATINGPHYSICIANFIRSTNAME")
    private String treatingPhysicianFirstname;

    @CsvBindByName(column = "TREATINGPHYSICIANLASTNAME")
    private String treatingPhysicianLastname;

    @CsvBindByName(column = "TREATINGPHYSICIANSTREET")
    private String treatingPhysicianStreet;

    @CsvBindByName(column = "TREATINGPHYSICIANCITY")
    private String treatingPhysicianCity;

    @CsvBindByName(column = "TREATINGPHYSICIANSTATE")
    private String treatingPhysicianState;

    @CsvBindByName(column = "TREATINGPHYSICIANZIP")
    private String treatingPhysicianZip;

    @CsvBindByName(column = "TREATINGPHYSICIANPHONE")
    private String treatingPhysicianPhone;

    @CsvBindByName(column = "TREATINGHOSPITALNAME")
    private String treatingHospitalName;

    @CsvBindByName(column = "TREATINGHOSPITALSTREET")
    private String treatingHospitalStreet;

    @CsvBindByName(column = "TREATINGHOSPITALCITY")
    private String treatingHospitalCity;

    @CsvBindByName(column = "TREATINGHOSPITALSTATE")
    private String treatingHospitalState;

    @CsvBindByName(column = "TREATINGHOSPITALZIP")
    private String treatingHospitalZip;

    @CsvBindByName(column = "TREATINGHOSPITALPHONE")
    private String treatingHospitalPhone;

    @CsvCustomBindByName(column = "ERVISIT", converter = BooleanFieldConverter.class)
    private Boolean erVisit;

    @CsvCustomBindByName(column = "OVERNIGHTINPATIENT", converter = BooleanFieldConverter.class)
    private Boolean overnightInPatient;

    public String getIdFormId() {
        return idFormId;
    }

    public void setIdFormId(String idFormId) {
        this.idFormId = idFormId;
    }

    public String getGerFormId() {
        return gerFormId;
    }

    public void setGerFormId(String gerFormId) {
        this.gerFormId = gerFormId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityEmail() {
        return communityEmail;
    }

    public void setCommunityEmail(String communityEmail) {
        this.communityEmail = communityEmail;
    }

    public String getCommunityPhone() {
        return communityPhone;
    }

    public void setCommunityPhone(String communityPhone) {
        this.communityPhone = communityPhone;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getIndividualFirstname() {
        return individualFirstname;
    }

    public void setIndividualFirstname(String individualFirstname) {
        this.individualFirstname = individualFirstname;
    }

    public String getIndividualLastname() {
        return individualLastname;
    }

    public void setIndividualLastname(String individualLastname) {
        this.individualLastname = individualLastname;
    }

    public String getIndividualIdNumber() {
        return individualIdNumber;
    }

    public void setIndividualIdNumber(String individualIdNumber) {
        this.individualIdNumber = individualIdNumber;
    }

    public Calendar getIndividualDateOfBirth() {
        return individualDateOfBirth;
    }

    public void setIndividualDateOfBirth(Calendar individualDateOfBirth) {
        this.individualDateOfBirth = individualDateOfBirth;
    }

    public String getIndividualSsn() {
        return individualSsn;
    }

    public void setIndividualSsn(String individualSsn) {
        this.individualSsn = individualSsn;
    }

    public String getIndividualAddressStreet() {
        return individualAddressStreet;
    }

    public void setIndividualAddressStreet(String individualAddressStreet) {
        this.individualAddressStreet = individualAddressStreet;
    }

    public String getIndividualAddressCity() {
        return individualAddressCity;
    }

    public void setIndividualAddressCity(String individualAddressCity) {
        this.individualAddressCity = individualAddressCity;
    }

    public String getIndividualAddressState() {
        return individualAddressState;
    }

    public void setIndividualAddressState(String individualAddressState) {
        this.individualAddressState = individualAddressState;
    }

    public String getIndividualAddressZip() {
        return individualAddressZip;
    }

    public void setIndividualAddressZip(String individualAddressZip) {
        this.individualAddressZip = individualAddressZip;
    }

    public String getIndividualGenger() {
        return individualGenger;
    }

    public void setIndividualGenger(String individualGenger) {
        this.individualGenger = individualGenger;
    }

    public Integer getIndividualMaritalStatus() {
        return individualMaritalStatus;
    }

    public void setIndividualMaritalStatus(Integer individualMaritalStatus) {
        this.individualMaritalStatus = individualMaritalStatus;
    }

    public String getFormAuthorFirstName() {
        return formAuthorFirstName;
    }

    public void setFormAuthorFirstName(String formAuthorFirstName) {
        this.formAuthorFirstName = formAuthorFirstName;
    }

    public String getFormAuthorLastName() {
        return formAuthorLastName;
    }

    public void setFormAuthorLastName(String formAuthorLastName) {
        this.formAuthorLastName = formAuthorLastName;
    }

    public String getFormAuthorRole() {
        return formAuthorRole;
    }

    public void setFormAuthorRole(String formAuthorRole) {
        this.formAuthorRole = formAuthorRole;
    }

    public String getFormAuthorOrganization() {
        return formAuthorOrganization;
    }

    public void setFormAuthorOrganization(String formAuthorOrganization) {
        this.formAuthorOrganization = formAuthorOrganization;
    }

    public String getManagerFirstname() {
        return managerFirstname;
    }

    public void setManagerFirstname(String managerFirstname) {
        this.managerFirstname = managerFirstname;
    }

    public String getManagerLastname() {
        return managerLastname;
    }

    public void setManagerLastname(String managerLastname) {
        this.managerLastname = managerLastname;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getRnFirstname() {
        return rnFirstname;
    }

    public void setRnFirstname(String rnFirstname) {
        this.rnFirstname = rnFirstname;
    }

    public String getRnLastname() {
        return rnLastname;
    }

    public void setRnLastname(String rnLastname) {
        this.rnLastname = rnLastname;
    }

    public String getRnStreet() {
        return rnStreet;
    }

    public void setRnStreet(String rnStreet) {
        this.rnStreet = rnStreet;
    }

    public String getRnCity() {
        return rnCity;
    }

    public void setRnCity(String rnCity) {
        this.rnCity = rnCity;
    }

    public String getRnState() {
        return rnState;
    }

    public void setRnState(String rnState) {
        this.rnState = rnState;
    }

    public String getRnZip() {
        return rnZip;
    }

    public void setRnZip(String rnZip) {
        this.rnZip = rnZip;
    }

    public Calendar getEventDate() {
        return eventDate;
    }

    public void setEventDate(Calendar eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getEventMonth() {
        return eventMonth;
    }

    public void setEventMonth(Integer eventMonth) {
        this.eventMonth = eventMonth;
    }

    public Integer getEventDay() {
        return eventDay;
    }

    public void setEventDay(Integer eventDay) {
        this.eventDay = eventDay;
    }

    public Integer getEventYear() {
        return eventYear;
    }

    public void setEventYear(Integer eventYear) {
        this.eventYear = eventYear;
    }

    public Calendar getEventTime() {
        return eventTime;
    }

    public void setEventTime(Calendar eventTime) {
        this.eventTime = eventTime;
    }

    public Boolean getHasInjury() {
        return hasInjury;
    }

    public void setHasInjury(Boolean hasInjury) {
        this.hasInjury = hasInjury;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Calendar getEventDeathDiscoveredDate() {
        return eventDeathDiscoveredDate;
    }

    public void setEventDeathDiscoveredDate(Calendar eventDeathDiscoveredDate) {
        this.eventDeathDiscoveredDate = eventDeathDiscoveredDate;
    }

    public Calendar getEventDeathDiscoveredTime() {
        return eventDeathDiscoveredTime;
    }

    public void setEventDeathDiscoveredTime(Calendar eventDeathDiscoveredTime) {
        this.eventDeathDiscoveredTime = eventDeathDiscoveredTime;
    }

    public String getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(String personResponsible) {
        this.personResponsible = personResponsible;
    }

    public Integer getOtherEventType() {
        return otherEventType;
    }

    public void setOtherEventType(Integer otherEventType) {
        this.otherEventType = otherEventType;
    }

    public Integer getOtherEventSubType() {
        return otherEventSubType;
    }

    public void setOtherEventSubType(Integer otherEventSubType) {
        this.otherEventSubType = otherEventSubType;
    }

    public String getSituationNarrative() {
        return situationNarrative;
    }

    public void setSituationNarrative(String situationNarrative) {
        this.situationNarrative = situationNarrative;
    }

    public String getBackgroundNarrative() {
        return backgroundNarrative;
    }

    public void setBackgroundNarrative(String backgroundNarrative) {
        this.backgroundNarrative = backgroundNarrative;
    }

    public String getAssessmentNarrative() {
        return assessmentNarrative;
    }

    public void setAssessmentNarrative(String assessmentNarrative) {
        this.assessmentNarrative = assessmentNarrative;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getFollowUpDetails() {
        return followUpDetails;
    }

    public void setFollowUpDetails(String followUpDetails) {
        this.followUpDetails = followUpDetails;
    }

    public Boolean getFollowUpExpected() {
        return followUpExpected;
    }

    public void setFollowUpExpected(Boolean followUpExpected) {
        this.followUpExpected = followUpExpected;
    }

    public String getTreatingPhysicianFirstname() {
        return treatingPhysicianFirstname;
    }

    public void setTreatingPhysicianFirstname(String treatingPhysicianFirstname) {
        this.treatingPhysicianFirstname = treatingPhysicianFirstname;
    }

    public String getTreatingPhysicianLastname() {
        return treatingPhysicianLastname;
    }

    public void setTreatingPhysicianLastname(String treatingPhysicianLastname) {
        this.treatingPhysicianLastname = treatingPhysicianLastname;
    }

    public String getTreatingPhysicianStreet() {
        return treatingPhysicianStreet;
    }

    public void setTreatingPhysicianStreet(String treatingPhysicianStreet) {
        this.treatingPhysicianStreet = treatingPhysicianStreet;
    }

    public String getTreatingPhysicianCity() {
        return treatingPhysicianCity;
    }

    public void setTreatingPhysicianCity(String treatingPhysicianCity) {
        this.treatingPhysicianCity = treatingPhysicianCity;
    }

    public String getTreatingPhysicianState() {
        return treatingPhysicianState;
    }

    public void setTreatingPhysicianState(String treatingPhysicianState) {
        this.treatingPhysicianState = treatingPhysicianState;
    }

    public String getTreatingPhysicianZip() {
        return treatingPhysicianZip;
    }

    public void setTreatingPhysicianZip(String treatingPhysicianZip) {
        this.treatingPhysicianZip = treatingPhysicianZip;
    }

    public String getTreatingPhysicianPhone() {
        return treatingPhysicianPhone;
    }

    public void setTreatingPhysicianPhone(String treatingPhysicianPhone) {
        this.treatingPhysicianPhone = treatingPhysicianPhone;
    }

    public String getTreatingHospitalName() {
        return treatingHospitalName;
    }

    public void setTreatingHospitalName(String treatingHospitalName) {
        this.treatingHospitalName = treatingHospitalName;
    }

    public String getTreatingHospitalStreet() {
        return treatingHospitalStreet;
    }

    public void setTreatingHospitalStreet(String treatingHospitalStreet) {
        this.treatingHospitalStreet = treatingHospitalStreet;
    }

    public String getTreatingHospitalCity() {
        return treatingHospitalCity;
    }

    public void setTreatingHospitalCity(String treatingHospitalCity) {
        this.treatingHospitalCity = treatingHospitalCity;
    }

    public String getTreatingHospitalState() {
        return treatingHospitalState;
    }

    public void setTreatingHospitalState(String treatingHospitalState) {
        this.treatingHospitalState = treatingHospitalState;
    }

    public String getTreatingHospitalZip() {
        return treatingHospitalZip;
    }

    public void setTreatingHospitalZip(String treatingHospitalZip) {
        this.treatingHospitalZip = treatingHospitalZip;
    }

    public String getTreatingHospitalPhone() {
        return treatingHospitalPhone;
    }

    public void setTreatingHospitalPhone(String treatingHospitalPhone) {
        this.treatingHospitalPhone = treatingHospitalPhone;
    }


    public Boolean getErVisit() {
        return erVisit;
    }

    public void setErVisit(Boolean erVisit) {
        this.erVisit = erVisit;
    }

    public Boolean getOvernightInPatient() {
        return overnightInPatient;
    }

    public void setOvernightInPatient(Boolean overnightInPatient) {
        this.overnightInPatient = overnightInPatient;
    }

    @Override
    public String toString() {
        return "TherapEventCSV{" +
                "idFormId='" + idFormId + '\'' +
                ", gerFormId='" + gerFormId + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", organizationPhone='" + organizationPhone + '\'' +
                ", organizationEmail='" + organizationEmail + '\'' +
                ", communityName='" + communityName + '\'' +
                ", communityId='" + communityId + '\'' +
                ", communityEmail='" + communityEmail + '\'' +
                ", communityPhone='" + communityPhone + '\'' +
                ", siteId='" + siteId + '\'' +
                ", siteName='" + siteName + '\'' +
                ", individualFirstname='" + individualFirstname + '\'' +
                ", individualLastname='" + individualLastname + '\'' +
                ", individualIdNumber='" + individualIdNumber + '\'' +
                ", individualDateOfBirth=" + individualDateOfBirth +
                ", individualSsn='" + individualSsn + '\'' +
                ", individualAddressStreet='" + individualAddressStreet + '\'' +
                ", individualAddressCity='" + individualAddressCity + '\'' +
                ", individualAddressState='" + individualAddressState + '\'' +
                ", individualAddressZip='" + individualAddressZip + '\'' +
                ", individualGenger='" + individualGenger + '\'' +
                ", individualMaritalStatus=" + individualMaritalStatus +
                ", formAuthorFirstName='" + formAuthorFirstName + '\'' +
                ", formAuthorLastName='" + formAuthorLastName + '\'' +
                ", formAuthorRole='" + formAuthorRole + '\'' +
                ", formAuthorOrganization='" + formAuthorOrganization + '\'' +
                ", managerFirstname='" + managerFirstname + '\'' +
                ", managerLastname='" + managerLastname + '\'' +
                ", managerPhone='" + managerPhone + '\'' +
                ", managerEmail='" + managerEmail + '\'' +
                ", rnFirstname='" + rnFirstname + '\'' +
                ", rnLastname='" + rnLastname + '\'' +
                ", rnStreet='" + rnStreet + '\'' +
                ", rnCity='" + rnCity + '\'' +
                ", rnState='" + rnState + '\'' +
                ", rnZip='" + rnZip + '\'' +
                ", eventDate=" + eventDate +
                ", eventMonth=" + eventMonth +
                ", eventDay=" + eventDay +
                ", eventYear=" + eventYear +
                ", eventTime=" + eventTime +
                ", hasInjury=" + hasInjury +
                ", eventLocation='" + eventLocation + '\'' +
                ", eventType=" + eventType +
                ", eventDeathDiscoveredDate=" + eventDeathDiscoveredDate +
                ", eventDeathDiscoveredTime=" + eventDeathDiscoveredTime +
                ", personResponsible='" + personResponsible + '\'' +
                ", otherEventType=" + otherEventType +
                ", otherEventSubType=" + otherEventSubType +
                ", situationNarrative='" + situationNarrative + '\'' +
                ", backgroundNarrative='" + backgroundNarrative + '\'' +
                ", assessmentNarrative='" + assessmentNarrative + '\'' +
                ", enteredBy='" + enteredBy + '\'' +
                ", followUpDetails='" + followUpDetails + '\'' +
                ", followUpExpected=" + followUpExpected +
                ", treatingPhysicianFirstname='" + treatingPhysicianFirstname + '\'' +
                ", treatingPhysicianLastname='" + treatingPhysicianLastname + '\'' +
                ", treatingPhysicianStreet='" + treatingPhysicianStreet + '\'' +
                ", treatingPhysicianCity='" + treatingPhysicianCity + '\'' +
                ", treatingPhysicianState='" + treatingPhysicianState + '\'' +
                ", treatingPhysicianZip='" + treatingPhysicianZip + '\'' +
                ", treatingPhysicianPhone='" + treatingPhysicianPhone + '\'' +
                ", treatingHospitalName='" + treatingHospitalName + '\'' +
                ", treatingHospitalStreet='" + treatingHospitalStreet + '\'' +
                ", treatingHospitalCity='" + treatingHospitalCity + '\'' +
                ", treatingHospitalState='" + treatingHospitalState + '\'' +
                ", treatingHospitalZip='" + treatingHospitalZip + '\'' +
                ", treatingHospitalPhone='" + treatingHospitalPhone + '\'' +
                ", erVisit=" + erVisit +
                ", overnightInPatient=" + overnightInPatient +
                '}';
    }
}