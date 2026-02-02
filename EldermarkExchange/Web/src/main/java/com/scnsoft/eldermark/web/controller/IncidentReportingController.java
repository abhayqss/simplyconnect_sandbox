package com.scnsoft.eldermark.web.controller;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.basic.Response;
import com.scnsoft.eldermark.facades.IncidentFacadeWeb;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Api(value = "IncidentReport", description = "Incident Report")
//@Validated
//@Controller
//@RequestMapping("/ir")
public class IncidentReportingController {
    
    @Autowired
    private IncidentFacadeWeb incidentFacade;
    
    @ResponseBody
    @RequestMapping(value="/events/{eventId}/pdf-incident-report", method=RequestMethod.GET)
    public Response getIncidentReportPDF(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId, @RequestParam("timeZoneOffset") Integer timeZoneOffset,
            HttpServletResponse response) throws DocumentException, IOException {
        incidentFacade.getIncidentReportPDF(response, eventId, timeZoneOffset);
        return Response.successResponse();
    }
    
    @ResponseBody
    @RequestMapping(value="/events/{eventId}/initialized-incident-report", method=RequestMethod.GET)
    public Response<IncidentReportDto> getInitializedIncidentReport(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId) throws DocumentException, IOException {
        return Response.successResponse(incidentFacade.initIncidentReport(eventId));
    }
    
    @ResponseBody
    @RequestMapping(value="/incident-reports/{id}", method=RequestMethod.GET)
    public Response<IncidentReportDto> getIncidentReportDetails(@ApiParam(value = "incident report id", required = true) @PathVariable("id") Long id) {
        return Response.successResponse(incidentFacade.getIncidentReportDetails(id));
    }
    
    @ResponseBody
    @RequestMapping(value="/events/{eventId}/incident-report-drafts", method={RequestMethod.PUT, RequestMethod.POST},
    consumes=MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveIncidentReportDraft(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
            @ApiParam(value = "incident report data", required = true) @RequestBody IncidentReportDto incidentReportDto) throws Exception {
        Long id = incidentFacade.saveIncidentReportDraft(eventId, incidentReportDto);
        return Response.successResponse(id);
    }
    
    @ResponseBody
    @RequestMapping(value="/events/{eventId}/incident-reports", method=RequestMethod.POST)
    public Response<Long> submitIncidentReport(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
            @ApiParam(value = "incident report data", required = true) @RequestBody IncidentReportDto incidentReportDto) throws Exception {
        Long id = incidentFacade.submitIncidentReport(eventId, incidentReportDto);
        return Response.successResponse(id);
    }

    @ResponseBody
    @RequestMapping("/events/{eventId}/can-create-incident-report")
    public Response<Boolean> canCreateIncidentReport(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId) {
        Boolean result = incidentFacade.canCurrentUserCreateIncidentReport(eventId);
        return Response.successResponse(result);
    }
}
