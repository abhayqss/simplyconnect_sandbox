package com.scnsoft.eldermark.web.controller;

import java.util.List;

import com.scnsoft.eldermark.shared.StateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scnsoft.eldermark.dto.basic.Response;
import com.scnsoft.eldermark.dto.dictionary.FreeTextKeyValueDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentTypeDto;
import com.scnsoft.eldermark.facades.DirectoryFacadeWeb;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@Api(value = "Directory", description = "Directory")
//@RestController
//@RequestMapping("ir/directory")
public class DirectoryController {
    
    @Autowired
    private DirectoryFacadeWeb dictionaryFacadeWeb;
    

    @ApiOperation(value = "Get Class Member Types for dropdown field",
        notes = "Get Class Member Types for dropdown field")
    @RequestMapping(value="/class-member-types" , method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getClassMemberTypes() {
        return Response.successResponse(dictionaryFacadeWeb.getClassMemberTypes());
    }
    

    @ApiOperation(value = "Get Incident Place Type for dropdown field",
        notes = "Get Incident Place Type for dropdown field")
    @RequestMapping(value="/incident-places", method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<List<FreeTextKeyValueDto>> getIncidentPlaces() {
        return Response.successResponse(dictionaryFacadeWeb.getIncidentPlaceTypes());
    }
    
    @ApiOperation(value = "Get Race for dropdown field",
        notes = "Get Race for dropdown field")
    @RequestMapping(value="/races" , method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getRaces(){
        return Response.successResponse(dictionaryFacadeWeb.getRaces());
    }
   

    @ApiOperation(value = "Get Incident Type Help for dropdown field",
        notes = "Get Incident Type Help for dropdown field")
    @RequestMapping(value="/incident-level-reporting-settings", method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<IncidentLevelReportingSettingsDto> getIncidentLevelReportingSettings(
    @ApiParam(value = "incident level", required = true)
    @RequestParam("level") Integer level) {
        return Response.successResponse(dictionaryFacadeWeb.getIncidentTypeHelp(level));
    }
    

    @ApiOperation(value = "Get Incident Type as per incident level",
        notes = "Get Incident Type as per incident level")
    @RequestMapping(value="/incident-types", method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<List<IncidentTypeDto>> getIncidentTypes(
    @ApiParam(value = "incident level")
    @RequestParam(value = "level", required = false) Integer level) {
        return Response.successResponse(dictionaryFacadeWeb.getIncidentTypes(level));
    }

    @ApiOperation(value = "Get Genders for dropdown field",
            notes = "Get Genders for dropdown field")
    @RequestMapping(value="/genders", method=RequestMethod.GET)
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getGenders(){
        return Response.successResponse(dictionaryFacadeWeb.getGenders());
    }

    @ApiOperation(value = "Get States for dropdown field",
            notes = "Get States for dropdown field")
    @RequestMapping(value="/states", method=RequestMethod.GET)
    public Response<List<StateDto>> getUSStates() {
        final List<StateDto> dto = dictionaryFacadeWeb.getStates();
        return Response.successResponse(dto);
    }

}
