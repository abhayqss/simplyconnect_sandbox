package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IncidentReportHistoryListItemDto;
import com.scnsoft.eldermark.dto.IncidentReportListItemDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.IncidentReportFacade;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/incident-reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class IncidentReportController {

    @Autowired
    private IncidentReportFacade incidentReportFacade;

    @GetMapping
    public Response<List<IncidentReportListItemDto>> find(@Valid @ModelAttribute IncidentReportFilter filter, Pageable pageable) {
        return Response.pagedResponse(incidentReportFacade.find(filter, pageable));
    }

    @GetMapping(value = "/{id}")
    public Response<IncidentReportDto> findById(@PathVariable("id") Long id) {
        return Response.successResponse(incidentReportFacade.findById(id));
    }

    @GetMapping(value = "/{id}/history")
    public Response<List<IncidentReportHistoryListItemDto>> findHistoryById(@PathVariable("id") Long id, Pageable pageable) {
        return Response.pagedResponse(incidentReportFacade.findHistoryById(id, pageable));
    }

    @GetMapping(value = "/{id}/download")
    public void downloadById(@PathVariable("id") Long id, @RequestHeader("timeZoneOffset") Integer timeZoneOffset, HttpServletResponse response) {
        incidentReportFacade.downloadById(id, response, DateTimeUtils.generateZoneOffset(timeZoneOffset));
    }

    @GetMapping(value = "/default")
    public Response<IncidentReportDto> findDefault(@RequestParam("eventId") Long eventId) {
        return Response.successResponse(incidentReportFacade.findDefault(eventId));
    }

    @DeleteMapping(value = "/{id}")
    public Response<Void> delete(@PathVariable("id") Long id) {
        incidentReportFacade.deleteById(id);
        return Response.successResponse();
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST})
    public Response<Long> saveDraft(@Valid @ModelAttribute IncidentReportDto incidentReportDto) {
        Long id = incidentReportFacade.saveDraft(incidentReportDto);
        return Response.successResponse(id);
    }

    @RequestMapping(value = "/submit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Response<Long> submit(@Validated(ValidationGroups.Update.class) @ModelAttribute IncidentReportDto incidentReportDto) {
        Long id = incidentReportFacade.submit(incidentReportDto);
        return Response.successResponse(id);
    }

    @GetMapping("/can-view")
    public Response<Boolean> canView(@RequestParam(name = "clientId", required = false) Long clientId) {
        if (clientId == null) {
            return Response.successResponse(incidentReportFacade.canViewList());
        } else {
            return Response.successResponse(incidentReportFacade.canViewByClientId(clientId));
        }
    }

    @GetMapping(value = "/incident-pictures/{pictureId}")
    public Response<byte[]> downloadIncidentPictureById(@PathVariable("pictureId") Long pictureId) {
        var picture = incidentReportFacade.downloadIncidentPictureById(pictureId);
        return Response.successResponse(new Response.Body<>(picture.getBytes(), picture.getMediaType()));
    }

    @GetMapping(value = "/oldest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findOldestDate(@RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(incidentReportFacade.findOldestDateByOrganization(organizationId));
    }

    @GetMapping(value = "/newest/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findNewestDate(@RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(incidentReportFacade.findNewestDateByOrganization(organizationId));
    }

    @PostMapping("/{id}/conversation/join")
    public Response<Void> joinConversation(@PathVariable("id") Long id) {
        incidentReportFacade.joinConversation(id);
        return Response.successResponse();
    }
}
