package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.service.EventTypesService;
import com.scnsoft.eldermark.service.PhysiciansService;
import com.scnsoft.eldermark.service.VitalSignService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CareTeamRoleService;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeGroupDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Public info controller.
 *
 * @author phomal
 * Created by phomal on 2/16/2018.
 */
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-16T18:53:11.881+03:00")
@Api(value = "Info", description = "General information (public access)")
@RestController
@RequestMapping("/info")
public class InfoController {

    final Logger logger = Logger.getLogger(InfoController.class.getName());

    private final VitalSignService vitalSignService;
    private final PhysiciansService physiciansService;
    private final EventTypesService eventTypesService;
    private final CareTeamRoleService careTeamRoleService;
    private final StateService stateService;

    @Autowired
    public InfoController(VitalSignService vitalSignService, PhysiciansService physiciansService, EventTypesService eventTypesService, CareTeamRoleService careTeamRoleService, StateService stateService) {
        this.vitalSignService = vitalSignService;
        this.physiciansService = physiciansService;
        this.eventTypesService = eventTypesService;
        this.careTeamRoleService = careTeamRoleService;
        this.stateService = stateService;
    }

    @ApiOperation(value = "List care team roles", notes = "<h3>Sorting rules</h3><p>The data is not ordered.</p>")
    @GetMapping(value = "/careTeamRoles")
    public Response<List<CareTeamRoleDto>> getCareTeamRoles() {
        final List<CareTeamRoleDto> dto = careTeamRoleService.getAllCareTeamRoles();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List event groups", notes = "<h3>Sorting rules</h3><p>The data is sorted by group priority (from the highest to the lowest)</p>")
    @GetMapping(value = "/eventgroups")
    public Response<List<EventTypeGroupDto>> getEventGroups() {
        final List<EventTypeGroupDto> dto = eventTypesService.getEventGroups();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List event types")
    @GetMapping(value = "/eventtypes")
    public Response<List<EventTypeDto>> getEventTypes() {
        final List<EventTypeDto> dto = eventTypesService.getEventTypes();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List US states")
    @GetMapping(value = "/usStates")
    public Response<List<StateDto>> getUSStates() {
        final List<StateDto> dto = stateService.getAllStates();
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List vital sign types", notes = "Returns a human-readable description of all Vital Sign types in use")
    @GetMapping(value = "/vitalSigns")
    public Response<Map<VitalSignType, String>> getVitalSignTypes() {
        final Map<VitalSignType, String> dto = vitalSignService.getVitalSigns();
        return Response.successResponse(dto);
    }

}
