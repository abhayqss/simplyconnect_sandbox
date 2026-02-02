package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.projection.EncounterNoteDetailsAware;
import com.scnsoft.eldermark.beans.projection.EventDetailsAware;
import com.scnsoft.eldermark.beans.projection.NoteOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.projection.OutreachReportAssessmentDataAware;
import com.scnsoft.eldermark.beans.projection.ServicePlanDetailsAware;
import com.scnsoft.eldermark.beans.security.projection.ClientCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.beans.security.projection.CommunityCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.client.report.ClientDetailsOutreachReportItem;

import java.util.List;
import java.util.Map;

public class OutreachReturnTrackerReportDto {
    private List<ClientDetailsOutreachReportItem> clientData;
    private List<NoteOutreachReportDetailsAware> notes;
    private Map<Long, List<EncounterNoteDetailsAware>> encounterNotes;
    private List<EventDetailsAware> events;
    private List<OutreachReportAssessmentDataAware> assessments;
    private List<ServicePlanDetailsAware> servicePlans;
    private List<CommunityCareTeamMemberOutreachReportDetailsAware> careTeamMembers;
    private List<ClientCareTeamMemberOutreachReportDetailsAware> clientCareTeamMembers;
    private Map<Long, List<PersonAddress>> clientPersonAddresses;
    private Map<Long, List<PersonTelecom>> clientPersonTelecoms;
    private Map<Long, List<PersonTelecom>> ctmTelecoms;
    private Map<Long, List<PersonTelecom>> clientCtmTelecoms;

    public List<NoteOutreachReportDetailsAware> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteOutreachReportDetailsAware> notes) {
        this.notes = notes;
    }

    public List<EventDetailsAware> getEvents() {
        return events;
    }

    public void setEvents(List<EventDetailsAware> events) {
        this.events = events;
    }

    public List<OutreachReportAssessmentDataAware> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<OutreachReportAssessmentDataAware> assessments) {
        this.assessments = assessments;
    }

    public List<ServicePlanDetailsAware> getServicePlans() {
        return servicePlans;
    }

    public void setServicePlans(List<ServicePlanDetailsAware> servicePlans) {
        this.servicePlans = servicePlans;
    }

    public List<CommunityCareTeamMemberOutreachReportDetailsAware> getCareTeamMembers() {
        return careTeamMembers;
    }

    public void setCareTeamMembers(List<CommunityCareTeamMemberOutreachReportDetailsAware> careTeamMembers) {
        this.careTeamMembers = careTeamMembers;
    }

    public Map<Long, List<EncounterNoteDetailsAware>> getEncounterNotes() {
        return encounterNotes;
    }

    public void setEncounterNotes(Map<Long, List<EncounterNoteDetailsAware>> encounterNotes) {
        this.encounterNotes = encounterNotes;
    }

    public List<ClientDetailsOutreachReportItem> getClientData() {
        return clientData;
    }

    public void setClientData(List<ClientDetailsOutreachReportItem> clientData) {
        this.clientData = clientData;
    }

    public Map<Long, List<PersonAddress>> getClientPersonAddresses() {
        return clientPersonAddresses;
    }

    public void setClientPersonAddresses(Map<Long, List<PersonAddress>> clientPersonAddresses) {
        this.clientPersonAddresses = clientPersonAddresses;
    }

    public Map<Long, List<PersonTelecom>> getClientPersonTelecoms() {
        return clientPersonTelecoms;
    }

    public void setClientPersonTelecoms(Map<Long, List<PersonTelecom>> clientPersonTelecoms) {
        this.clientPersonTelecoms = clientPersonTelecoms;
    }

    public List<ClientCareTeamMemberOutreachReportDetailsAware> getClientCareTeamMembers() {
        return clientCareTeamMembers;
    }

    public void setClientCareTeamMembers(List<ClientCareTeamMemberOutreachReportDetailsAware> clientCareTeamMembers) {
        this.clientCareTeamMembers = clientCareTeamMembers;
    }

    public Map<Long, List<PersonTelecom>> getCtmTelecoms() {
        return ctmTelecoms;
    }

    public void setCtmTelecoms(Map<Long, List<PersonTelecom>> ctmTelecoms) {
        this.ctmTelecoms = ctmTelecoms;
    }

    public Map<Long, List<PersonTelecom>> getClientCtmTelecoms() {
        return clientCtmTelecoms;
    }

    public void setClientCtmTelecoms(Map<Long, List<PersonTelecom>> clientCtmTelecoms) {
        this.clientCtmTelecoms = clientCtmTelecoms;
    }
}