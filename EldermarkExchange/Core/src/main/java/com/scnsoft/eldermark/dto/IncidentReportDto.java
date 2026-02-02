package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.dto.dictionary.TextDto;

import java.util.List;

public class IncidentReportDto {
    private Long id;
    private String clientName;
    private Long clientClassMemberTypeId;
    private String clientRIN;
    private Long clientBirthDate;
    private Long clientGenderId;
    private Long clientRaceId;
    private Long clientTransitionToCommunityDate;
    private String clientClassMemberCurrentAddress;
    private List<String> currentDiagnoses;
    private List<String> activeMedications;
    private String agencyName;
    private String agencyAddress;
    private String qualityAdministrator;
    private String careManagerOrStaffWithPrimServRespAndTitle;
    private String careManagerOrStaffPhone;
    private String careManagerOrStaffEmail;
    private String mcoCareCoordinatorAndAgency;
    private String mcoCareCoordinatorPhone;
    private String mcoCareCoordinatorEmail;
    private Long incidentDateTime;
    private Long incidentDiscoveredDate;
    private Boolean wasProviderPresentOrScheduled;
    private List<TextDto> incidentPlaces;
    private List<IndividualDto> incidentInvolvedIndividuals;
    private List<TextDto> level1IncidentTypes;
    private List<TextDto> level2IncidentTypes;
    private List<TextDto> level3IncidentTypes;
    private Boolean wasIncidentCausedBySubstance;
    private String incidentNarrative;
    private String agencyResponseToIncident;
    private String reportAuthor;
    private Long reportCompletedDate;
    private Long reportDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getClientClassMemberTypeId() {
        return clientClassMemberTypeId;
    }

    public void setClientClassMemberTypeId(Long clientClassMemberTypeId) {
        this.clientClassMemberTypeId = clientClassMemberTypeId;
    }

    public String getClientRIN() {
        return clientRIN;
    }

    public void setClientRIN(String clientRIN) {
        this.clientRIN = clientRIN;
    }

    public Long getClientBirthDate() {
        return clientBirthDate;
    }

    public void setClientBirthDate(Long clientBirthDate) {
        this.clientBirthDate = clientBirthDate;
    }

    public Long getClientGenderId() {
        return clientGenderId;
    }

    public void setClientGenderId(Long clientGenderId) {
        this.clientGenderId = clientGenderId;
    }

    public Long getClientRaceId() {
        return clientRaceId;
    }

    public void setClientRaceId(Long clientRaceId) {
        this.clientRaceId = clientRaceId;
    }

    public Long getClientTransitionToCommunityDate() {
        return clientTransitionToCommunityDate;
    }

    public void setClientTransitionToCommunityDate(Long clientTransitionToCommunityDate) {
        this.clientTransitionToCommunityDate = clientTransitionToCommunityDate;
    }

    public String getClientClassMemberCurrentAddress() {
        return clientClassMemberCurrentAddress;
    }

    public void setClientClassMemberCurrentAddress(String clientClassMemberCurrentAddress) {
        this.clientClassMemberCurrentAddress = clientClassMemberCurrentAddress;
    }

    public List<String> getCurrentDiagnoses() {
        return currentDiagnoses;
    }

    public void setCurrentDiagnoses(List<String> currentDiagnoses) {
        this.currentDiagnoses = currentDiagnoses;
    }

    public List<String> getActiveMedications() {
        return activeMedications;
    }

    public void setActiveMedications(List<String> activeMedications) {
        this.activeMedications = activeMedications;
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

    public Long getIncidentDateTime() {
        return incidentDateTime;
    }

    public void setIncidentDateTime(Long incidentDateTime) {
        this.incidentDateTime = incidentDateTime;
    }

    public Long getIncidentDiscoveredDate() {
        return incidentDiscoveredDate;
    }

    public void setIncidentDiscoveredDate(Long incidentDiscoveredDate) {
        this.incidentDiscoveredDate = incidentDiscoveredDate;
    }

    public Boolean getWasProviderPresentOrScheduled() {
        return wasProviderPresentOrScheduled;
    }

    public void setWasProviderPresentOrScheduled(Boolean wasProviderPresentOrScheduled) {
        this.wasProviderPresentOrScheduled = wasProviderPresentOrScheduled;
    }

    public List<TextDto> getIncidentPlaces() {
        return incidentPlaces;
    }

    public void setIncidentPlaces(List<TextDto> incidentPlaces) {
        this.incidentPlaces = incidentPlaces;
    }

    public List<IndividualDto> getIncidentInvolvedIndividuals() {
        return incidentInvolvedIndividuals;
    }

    public void setIncidentInvolvedIndividuals(List<IndividualDto> incidentInvolvedIndividuals) {
        this.incidentInvolvedIndividuals = incidentInvolvedIndividuals;
    }

    public List<TextDto> getLevel1IncidentTypes() {
        return level1IncidentTypes;
    }

    public void setLevel1IncidentTypes(List<TextDto> level1IncidentTypes) {
        this.level1IncidentTypes = level1IncidentTypes;
    }

    public List<TextDto> getLevel2IncidentTypes() {
        return level2IncidentTypes;
    }

    public void setLevel2IncidentTypes(List<TextDto> level2IncidentTypes) {
        this.level2IncidentTypes = level2IncidentTypes;
    }

    public List<TextDto> getLevel3IncidentTypes() {
        return level3IncidentTypes;
    }

    public void setLevel3IncidentTypes(List<TextDto> level3IncidentTypes) {
        this.level3IncidentTypes = level3IncidentTypes;
    }

    public Boolean getWasIncidentCausedBySubstance() {
        return wasIncidentCausedBySubstance;
    }

    public void setWasIncidentCausedBySubstance(Boolean wasIncidentCausedBySubstance) {
        this.wasIncidentCausedBySubstance = wasIncidentCausedBySubstance;
    }

    public String getIncidentNarrative() {
        return incidentNarrative;
    }

    public void setIncidentNarrative(String incidentNarrative) {
        this.incidentNarrative = incidentNarrative;
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

    public Long getReportCompletedDate() {
        return reportCompletedDate;
    }

    public void setReportCompletedDate(Long reportCompletedDate) {
        this.reportCompletedDate = reportCompletedDate;
    }

    public Long getReportDate() {
        return reportDate;
    }

    public void setReportDate(Long reportDate) {
        this.reportDate = reportDate;
    }
}
