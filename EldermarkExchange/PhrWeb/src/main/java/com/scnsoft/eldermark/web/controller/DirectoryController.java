package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.dictionary.FreeTextKeyValueDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentTypeDto;
import com.scnsoft.eldermark.facade.DirectoryFacade;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.util.List;

@Api(value = "Dictionary", description = "Dictionary")
@RestController
@RequestMapping("phr/directory")
public class DirectoryController {
    
    @Autowired
    private DirectoryFacade dictionaryFacade;
    
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Class Member Types for dropdown field",
        notes = "Get Class Member Types for dropdown field")
    @GetMapping("/class-member-types")
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getClassMemberTypes() {
        return Response.successResponse(dictionaryFacade.getClassMemberTypes());
    }
    
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Incident Place Type for dropdown field",
        notes = "Get Incident Place Type for dropdown field")
    @GetMapping("/incident-places")
    @Transactional(readOnly=true)
    public Response<List<FreeTextKeyValueDto>> getIncidentPlaces() {
        return Response.successResponse(dictionaryFacade.getIncidentPlaceTypes());
    }
    
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Race for dropdown field",
        notes = "Get Race for dropdown field")
    @GetMapping("/races")
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getRaces(){
        return Response.successResponse(dictionaryFacade.getRaces());
    }
   
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Incident Type Help for dropdown field",
        notes = "Get Incident Type Help for dropdown field")
    @GetMapping("/incident-level-reporting-settings")
    @Transactional(readOnly=true)
    public Response<IncidentLevelReportingSettingsDto> getIncidentLevelReportingSettings(
    @ApiParam(value = "incident level", required = true)
    @RequestParam("level") Integer level) {
        return Response.successResponse(dictionaryFacade.getIncidentTypeHelp(level));
    }
    
    @ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Incident Type as per incident level",
        notes = "Get Incident Type as per incident level")
    @GetMapping("/incident-types")
    @Transactional(readOnly=true)
    public Response<List<IncidentTypeDto>> getIncidentTypes(
    @ApiParam(value = "incident level")
    @RequestParam(value = "level", required = false) Integer level) {
        return Response.successResponse(dictionaryFacade.getIncidentTypes(level));
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class),
            @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Forbidden", response = ResponseErrorDto.class)
    })
    @ApiOperation(value = "Get Genders for dropdown field",
            notes = "Get Genders for dropdown field")
    @GetMapping("/genders")
    @Transactional(readOnly=true)
    public Response<List<KeyValueDto>> getGenders(){
        return Response.successResponse(dictionaryFacade.getGenders());
    }

}
