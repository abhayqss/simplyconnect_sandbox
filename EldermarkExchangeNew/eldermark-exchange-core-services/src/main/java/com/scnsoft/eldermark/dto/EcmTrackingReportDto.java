package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.projection.AssessmentDataAware;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamMemberIdNameAware;
import com.scnsoft.eldermark.beans.projection.ClientEncounterNoteAware;
import com.scnsoft.eldermark.beans.projection.ClientEventDateAware;
import com.scnsoft.eldermark.beans.projection.ClientInsuranceAuthorizationDetailsAware;
import com.scnsoft.eldermark.beans.projection.ClientNoteAware;
import com.scnsoft.eldermark.beans.projection.ServicePlanDetailsAware;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsItem;

import java.util.List;
import java.util.Map;

public class EcmTrackingReportDto {
    private List<ClientCareTeamMemberIdNameAware> careTeamMembers;
    private List<ClientDetailsItem> clientHistoryData;
    private List<AssessmentDataAware> assessments;
    private Map<Long, List<ClientNoteAware>> notes;
    private Map<Long, List<ClientEncounterNoteAware>> encounterNotes;
    private List<ClientEventDateAware> events;
    private List<ServicePlanDetailsAware> servicePlans;
    private List<ClientInsuranceAuthorizationDetailsAware> clientInsuranceAuthorizations;

    public List<ClientCareTeamMemberIdNameAware> getCareTeamMembers() {
        return careTeamMembers;
    }

    public void setCareTeamMembers(List<ClientCareTeamMemberIdNameAware> careTeamMembers) {
        this.careTeamMembers = careTeamMembers;
    }

    public List<ClientDetailsItem> getClientHistoryData() {
        return clientHistoryData;
    }

    public void setClientHistoryData(List<ClientDetailsItem> clientHistoryData) {
        this.clientHistoryData = clientHistoryData;
    }

    public List<AssessmentDataAware> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentDataAware> assessments) {
        this.assessments = assessments;
    }

    public Map<Long, List<ClientNoteAware>> getNotes() {
        return notes;
    }

    public void setNotes(Map<Long, List<ClientNoteAware>> notes) {
        this.notes = notes;
    }

    public Map<Long, List<ClientEncounterNoteAware>> getEncounterNotes() {
        return encounterNotes;
    }

    public void setEncounterNotes(Map<Long, List<ClientEncounterNoteAware>> encounterNotes) {
        this.encounterNotes = encounterNotes;
    }

    public List<ClientEventDateAware> getEvents() {
        return events;
    }

    public void setEvents(List<ClientEventDateAware> events) {
        this.events = events;
    }

    public List<ServicePlanDetailsAware> getServicePlans() {
        return servicePlans;
    }

    public void setServicePlans(List<ServicePlanDetailsAware> servicePlans) {
        this.servicePlans = servicePlans;
    }

    public List<ClientInsuranceAuthorizationDetailsAware> getClientInsuranceAuthorizations() {
        return clientInsuranceAuthorizations;
    }

    public void setClientInsuranceAuthorizations(List<ClientInsuranceAuthorizationDetailsAware> clientInsuranceAuthorizations) {
        this.clientInsuranceAuthorizations = clientInsuranceAuthorizations;
    }
}