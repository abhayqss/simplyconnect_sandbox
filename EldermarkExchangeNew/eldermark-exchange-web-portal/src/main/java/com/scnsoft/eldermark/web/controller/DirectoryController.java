package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.notes.NoteTypeDto;
import com.scnsoft.eldermark.dto.referral.ReferralGroupedCategoriesDto;
import com.scnsoft.eldermark.dto.serviceplan.DomainDto;
import com.scnsoft.eldermark.dto.serviceplan.PriorityDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramSubTypeDto;
import com.scnsoft.eldermark.dto.serviceplan.ProgramTypeDto;
import com.scnsoft.eldermark.facade.DirectoryFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/directory")
@RestController
public class DirectoryController {

    @Value("${videocall.max.participants}")
    private Integer maxCallParticipants;

    @Autowired
    private DirectoryFacade directoryFacade;

    @GetMapping(value = "/marital-status")
    public Response<List<KeyValueDto<Long>>> getMaritalStatus() {
        return Response.successResponse(directoryFacade.getMaritalStatus());
    }

    @GetMapping(value = "/genders")
    public Response<List<KeyValueDto<Long>>> getGenders(@RequestParam(name = "biologicalOnly", required = false) Boolean biologicalOnly) {
        return Response.successResponse(directoryFacade.getGenders(biologicalOnly));
    }

    @GetMapping(value = "/states")
    public Response<List<DirectoryStateListItemDto<Long>>> getStates() {
        return Response.successResponse(directoryFacade.getStates());
    }

    @GetMapping(value = "/races")
    public Response<List<IdentifiedTitledEntityDto>> getRaces() {
        return Response.successResponse(directoryFacade.getRaces());
    }

    @GetMapping(value = "/service-plan-domains", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DomainDto>> getServicePlanDomains() {
        return Response.successResponse(directoryFacade.getDomains());
    }

    @GetMapping(value = "/service-plan-priorities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<PriorityDto>> getServicePlanPriorities() {
        return Response.successResponse(directoryFacade.getPriorities());
    }

    @GetMapping(value = "/service-plan-program-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ProgramTypeDto>> getServicePlanProgramTypes() {
        return Response.successResponse(directoryFacade.getProgramTypes());
    }

    @GetMapping(value = "/service-plan-program-subtypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ProgramSubTypeDto>> getServicePlanProgramSubTypes() {
        return Response.successResponse(directoryFacade.getProgramSubTypes());
    }

    @GetMapping(value = "/system-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoleDto>> getSystemRoles(@RequestParam(name = "includeExternal", required = false) Boolean includeExternal) {
        return Response.successResponse(directoryFacade.getSystemRoles(includeExternal));
    }

    @RequestMapping(value = "/device-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DeviceTypeDto>> getDeviceTypeList() {
        return Response.successResponse(directoryFacade.getDeviceTypes());
    }

    @GetMapping(value = "/note-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NoteTypeDto>> getNoteTypes() {
        return Response.successResponse(directoryFacade.getNoteTypes());
    }

    @GetMapping(value = "/encounter-note-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getEncounterNoteTypes() {
        return Response.successResponse(directoryFacade.getEncounterNoteTypes());
    }

    @GetMapping(value = "/client-program-note-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedTitledEntityDto>> getClientProgramNoteTypes() {
        return Response.successResponse(directoryFacade.getClientProgramNoteTypes());
    }

    @GetMapping(value = "/employee-statuses")
    public Response<List<NamedTitledValueEntityDto<Integer>>> getEmployeeStatuses() {
        return Response.successResponse(directoryFacade.getEmployeeStatuses());
    }

    @GetMapping(value = "/client-statuses")
    public Response<List<NamedTitledEntityDto>> getClientStatuses() {
        return Response.successResponse(directoryFacade.getClientStatuses());
    }

    @GetMapping(value = "/prospect-statuses")
    public Response<List<NamedTitledEntityDto>> getProspectStatuses() {
        return Response.successResponse(directoryFacade.getProspectStatuses());
    }

    @GetMapping(value = "/related-party-relationships")
    public Response<List<NamedTitledEntityDto>> getRelatedPartyRelationships() {
        return Response.successResponse(directoryFacade.getRelatedPartyRelationships());
    }

    @GetMapping(value = "/referral-request-priorities")
    public Response<List<IdentifiedNamedTitledEntityDto>> getReferralPriorities() {
        return Response.successResponse(directoryFacade.getReferralPriorities());
    }

    @GetMapping(value = "/referral-request-intents")
    public Response<List<IdentifiedNamedTitledEntityDto>> getReferralIntents() {
        return Response.successResponse(directoryFacade.getReferralIntents());
    }

    @GetMapping(value = "/referral-categories-grouped")
    public Response<List<ReferralGroupedCategoriesDto>> getReferralCategoriesGrouped() {
        return Response.successResponse(directoryFacade.getReferralCategoriesGrouped());
    }

    @GetMapping(value = "/referral-decline-reasons")
    public Response<List<IdentifiedNamedTitledEntityDto>> getReferralDeclineReasons() {
        return Response.successResponse(directoryFacade.getReferralDeclineReasons());
    }

    @GetMapping(value = "/referral-statuses")
    public Response<List<NamedTitledEntityDto>> getReferralStatuses() {
        return Response.successResponse(directoryFacade.getReferralStatuses());
    }

    @GetMapping(value = "/lab-research/reasons")
    public Response<List<NamedTitledEntityDto>> getLabResearchReasons() {
        return Response.successResponse(directoryFacade.getLabResearchReasons());
    }

    @GetMapping(value = "/lab-research/statuses")
    public Response<List<NamedTitledEntityDto>> getLabResearchOrderStatuses() {
        return Response.successResponse(directoryFacade.getLabResearchOrderStatuses());
    }

    @GetMapping(value = "/lab-research/policy-holder-relations")
    public Response<List<NamedTitledEntityDto>> getLabResearchOrderPolicyHolderRelations() {
        return Response.successResponse(directoryFacade.getLabResearchOrderPolicyHolderRelations());
    }


    @GetMapping(value = "/incident-types")
    public Response<List<IncidentTypeDto>> getIncidentTypes(
            @RequestParam(value = "level", required = false) Integer level) {
        return Response.successResponse(directoryFacade.getIncidentTypes(level));
    }


    @GetMapping(value = "/incident-level-reporting-settings")
    public Response<IncidentLevelReportingSettingsDto> getIncidentLevelReportingSettings(
            @RequestParam("level") Integer level) {
        return Response.successResponse(directoryFacade.getIncidentTypeHelp(level));
    }

    @GetMapping(value = "/incident-report-statuses")
    public Response<List<NamedTitledEntityDto>> getIncidentReportStatuses() {
        return Response.successResponse(directoryFacade.getIncidentReportStatuses());
    }


    @GetMapping(value = "/class-member-types")
    public Response<List<IdentifiedNamedEntityDto>> getClassMemberTypes() {
        return Response.successResponse(directoryFacade.getClassMemberTypes());
    }

    @GetMapping(value = "/incident-places")
    public Response<List<IdentifiedTitledValueEntityDto<Boolean>>> getIncidentPlaces() {
        return Response.successResponse(directoryFacade.getIncidentPlaceTypes());
    }

    @GetMapping(value = "/incident-weather-condition-types")
    public Response<List<IdentifiedTitledValueEntityDto<Boolean>>> getIncidentWeatherConditionTypes() {
        return Response.successResponse(directoryFacade.getIncidentWeatherTypes());
    }

    @GetMapping(value = "/max-participants-in-call")
    public Response<Integer> getMaxParticipantsInCall() {
        return Response.successResponse(maxCallParticipants);
    }

    @GetMapping(value = "/support-ticket-types")
    public Response<List<KeyValueDto<Long>>> getSupportTicketTypes() {
        return Response.successResponse(directoryFacade.getSupportTicketTypes());
    }

    @GetMapping(value = "/deactivation-reasons")
    public Response<List<NamedTitledEntityDto>> getDeactivationReasons() {
        return Response.successResponse(directoryFacade.getDeactivationReasons());
    }

    @GetMapping(value = "/client-expense-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NamedTitledEntityDto>> getAppointmentTypes() {
        return Response.successResponse(directoryFacade.getClientExpenseTypes());
    }

    @GetMapping(value = "/activity-types")
    public Response<List<AuditLogActionGroupDto>> getAuditLogActivityTypes() {
        return Response.successResponse(directoryFacade.getAuditLogActivityTypes());
    }
}
