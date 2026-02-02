package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.facade.NoteSubTypeFacade;
import com.scnsoft.eldermark.service.EventTypeService;
import com.scnsoft.eldermark.service.NotificationPreferencesService;
import com.scnsoft.eldermark.service.PhysiciansService;
import com.scnsoft.eldermark.service.VitalSignService;
import com.scnsoft.eldermark.service.passwords.PasswordValidationService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.EncounterNoteTypeService;
import com.scnsoft.eldermark.services.marketplace.MapsService;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeGroupDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.web.entity.InsuranceDto;
import com.scnsoft.eldermark.web.entity.PasswordRequirementsDto;
import com.scnsoft.eldermark.web.entity.SpecialityDto;
import com.scnsoft.eldermark.web.entity.notes.NoteSubTypeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author averazub
 * @author phomal
 * Created on 1/10/2017.
 */
@Api(value = "Public info", description = "General information")
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    VitalSignService vitalSignService;

    @Autowired
    PhysiciansService physiciansService;

    @Autowired
    EventTypeService eventTypeService;

    @Autowired
    StateService stateService;

    @Autowired
    PasswordValidationService passwordValidationService;

    @Autowired
    MapsService mapsService;

    @Autowired
    EncounterNoteTypeService encounterNoteTypeService;

    @Value("${update.request.serve.period.days}")
    Integer updateRequestServePeriod;

    @Autowired
    private NoteSubTypeFacade noteSubTypeFacade;

    @ApiOperation(value = "Get reference information on a vital sign")
    @GetMapping("/vitalSigns/{vitalSignType}/referenceInfo")
    public Response<String> getVitalSignReferenceInfo(
            @ApiParam(value = "Type of Vital Sign", required = true)
            @PathVariable(name = "vitalSignType") VitalSignType type) {
        return Response.successResponse(vitalSignService.getVitalSignReferenceInfo(type));
    }

    @ApiOperation(value = "Get a list of vital signs", notes = "Returns a human-readable description of all Vital Sign types in use")
    @GetMapping(value = "/vitalSigns")
    public Response<Map<VitalSignType, String>> getVitalSigns() {
        final Map<VitalSignType, String> dto = VitalSignService.getVitalSigns();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get the licence text")
    @GetMapping("/licenceInfo")
    public Response<String> getLicenceInfo() {
        return Response.successResponse("Text of licence Agreement");
    }

    @ApiOperation(value = "Get a list of event types")
    @GetMapping(value = "/eventtypes")
    public Response<List<EventTypeDto>> getEventTypes() {
        final List<EventTypeDto> dto = eventTypeService.getEventTypesForView();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of event groups",
            notes = "###Sorting rules\nThe data is sorted by group priority (from the highest to the lowest)")
    @GetMapping(value = "/eventgroups")
    public Response<List<EventTypeGroupDto>> getEventGroups() {
        final List<EventTypeGroupDto> dto = eventTypeService.getEventGroupsForView();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of physician specialities")
    @GetMapping(value = "/physicians/specialities")
    public Response<List<SpecialityDto>> getPhysicianSpecialities() {
        final List<SpecialityDto> dto = physiciansService.listPhysicianSpecialities();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of Physician In-Network Insurances")
    @GetMapping(value = "/physicians/insurances")
    public Response<List<InsuranceDto>> getPhysicianInsurances() {
        final List<InsuranceDto> dto = physiciansService.listPhysicianInsurances();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of US states")
    @GetMapping(value = "/usStates")
    public Response<List<StateDto>> getUSStates() {
        final List<StateDto> dto = stateService.getAllStates();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a handling time for Update Request")
    @GetMapping(value = "/updateRequest/servePeriod")
    public Response<Integer> getUpdateRequestServePeriod() {
        return Response.successResponse(updateRequestServePeriod);
    }

    @ApiOperation(value = "Get a list of Notification Channels")
    @GetMapping(value = "/notificationChannels")
    public Response<Map<NotificationType, String>> getNotificationChannels() {
        final Map<NotificationType, String> dto = NotificationPreferencesService.getPhrNotificationChannels();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get password requirements", notes = "Get password complexity requirements (in text and regexp)")
    @GetMapping(value = "/register/passwordRequirements")
    public Response<PasswordRequirementsDto> getPasswordRequirements() {
        final PasswordRequirementsDto dto = passwordValidationService.getPasswordRequirements();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get list of suggested cities\\states\\zip codes", notes = "Returns a list of suggested cities\\states\\zip codes for given search string")
    @GetMapping(value = "/addresses")
    public @ResponseBody
    Response<List<com.scnsoft.eldermark.shared.marketplace.AutoCompletedAddressInfoDto>> getAddresses(
            @NotNull @ApiParam(value = "address search string", required = true) @RequestParam(value = "searchText", required = true) String searchText
    ) {
        final List<com.scnsoft.eldermark.shared.marketplace.AutoCompletedAddressInfoDto> dto = mapsService.autoCompleteInUs(searchText);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Get a list of note sub types")
    @GetMapping(value = "/note-types")
    public Response<List<NoteSubTypeDto>> getNoteSubTypesUsingGET(

    ) {
        return Response.successResponse(noteSubTypeFacade.getAllPhrVisibleSubTypes());
    }

    @ApiOperation(value = "Get a list of encounter note types")
    @GetMapping(value = "/encounter-note-types")
    public Response<List<KeyValueDto>> getEncounterTypes() {
        return Response.successResponse(encounterNoteTypeService.getAllEncounterNoteTypes());
    }
}
