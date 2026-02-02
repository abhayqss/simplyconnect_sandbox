package com.scnsoft.eldermark.web.controller;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.facade.IncidentFacade;
import com.scnsoft.eldermark.shared.web.entity.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(value = "IncidentReport", description = "Incident Report")
@Validated
@RestController
@RequestMapping("/phr")
public class IncidentReportingController {

    @Autowired
    private IncidentFacade incidentFacade;

    @ResponseBody
    @RequestMapping(value = "/users/{userId}/events/{eventId}/pdf-incident-report", method = RequestMethod.GET)
    public void getIncidentReportPDF(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
                                     @ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
                                     @ApiParam(value = "Timezone Offset - the time difference between UTC time and local time, in minutes (example -120 for GMT+2)")
                                     @RequestParam(value = "timeZoneOffset", required = false) Integer timeZoneOffset,
                                     HttpServletResponse response) throws DocumentException, IOException {
        incidentFacade.getIncidentReportPDF(response, eventId, userId, timeZoneOffset);
    }

    @ResponseBody
    @RequestMapping(value = "/users/{userId}/events/{eventId}/pdf-incident-report/{incidentReportId}", method = RequestMethod.GET)
    public void getIncidentReportPDFById(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
                                         @ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
                                         @ApiParam(value = "event id", required = true) @PathVariable("incidentReportId") Long incidentReportId,
                                         @RequestParam(value = "timeZoneOffset", required = false) Integer timeZoneOffset,
                                         HttpServletResponse response) throws DocumentException, IOException {
        incidentFacade.getIncidentReportPDFById(response, userId, incidentReportId, timeZoneOffset);
    }

    @ResponseBody
    @RequestMapping(value = "/users/{userId}/events/{eventId}/initialized-incident-report", method = RequestMethod.GET)
    public Response<IncidentReportDto> getInitializedIncidentReport(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
                                                                    @ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId) throws DocumentException, IOException {
        return Response.successResponse(incidentFacade.initIncidentReport(eventId, userId));
    }

    @ResponseBody
    @RequestMapping(value = "/incident-reports/{id}", method = RequestMethod.GET)
    public Response<IncidentReportDto> getIncidentReportDetails(@ApiParam(value = "incident report id", required = true) @PathVariable("id") Long id) {
        return Response.successResponse(incidentFacade.getIncidentReportDetails(id));
    }

    @ResponseBody
    @RequestMapping(value = "/users/{userId}/events/{eventId}/incident-report-drafts", method = {RequestMethod.PUT, RequestMethod.POST})
    public Response<Long> saveIncidentReportDraft(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
                                                  @ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
                                                  @ApiParam(value = "incident report data", required = true) @RequestBody IncidentReportDto incidentReportDto) throws Exception {
        Long id = incidentFacade.saveIncidentReportDraft(userId, eventId, incidentReportDto);
        return Response.successResponse(id);
    }

    @ResponseBody
    @RequestMapping(value = "/users/{userId}/events/{eventId}/incident-reports", method = RequestMethod.POST)
    public Response<Long> submitIncidentReport(@ApiParam(value = "user id", required = true) @PathVariable("userId") Long userId,
                                               @ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId,
                                               @ApiParam(value = "incident report data", required = true) @RequestBody IncidentReportDto incidentReportDto) throws Exception {
        Long id = incidentFacade.submitIncidentReport(userId, eventId, incidentReportDto);
        return Response.successResponse(id);
    }

    @ResponseBody
    @GetMapping("/events/{eventId}/can-create-incident-report")
    public Response<Boolean> canCreateIncidentReport(@ApiParam(value = "event id", required = true) @PathVariable("eventId") Long eventId) {
        Boolean result = incidentFacade.canCurrentUserCreateIncidentReport(eventId);
        return Response.successResponse(result);
    }
}
