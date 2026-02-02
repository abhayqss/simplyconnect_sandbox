package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.dto.assessment.ClientAssessmentStatusCountDto;
import com.scnsoft.eldermark.dto.assessment.ClientDashboardAssessmentListItemDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.events.EventDashboardListItemDto;
import com.scnsoft.eldermark.dto.notes.NoteDashboardListItemDto;
import com.scnsoft.eldermark.dto.serviceplan.ClientDashboardServicePlanDto;
import com.scnsoft.eldermark.facade.ClientAssessmentFacade;
import com.scnsoft.eldermark.facade.ClientServicePlanFacade;
import com.scnsoft.eldermark.facade.EventFacade;
import com.scnsoft.eldermark.facade.NoteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/dashboard")
public class ClientDashboardController {

    @Autowired
    private ClientAssessmentFacade clientAssessmentFacade;

    @Autowired
    private ClientServicePlanFacade clientServicePlanFacade;

    @Autowired
    private EventFacade eventFacade;

    @Autowired
    private NoteFacade noteFacade;

    @GetMapping(value = "/assessments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientDashboardAssessmentListItemDto>> findAssessments(@PathVariable("clientId") Long clientId, Pageable pageable) {
        var assessments = clientAssessmentFacade.findForDashboard(clientId, pageable);
        return Response.pagedResponse(assessments);
    }

    @GetMapping(value = "/recent-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<EventDashboardListItemDto>> events(@PathVariable("clientId") Long clientId,
                                                            @RequestParam("limit") Integer limit) {
        var events = eventFacade.findEventsForDashboard(clientId, limit);
        return Response.successResponse(events);
    }

    @GetMapping(value = "/recent-notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NoteDashboardListItemDto>> notes(@PathVariable("clientId") Long clientId,
                                                          @RequestParam("limit") Integer limit) {
        var notes = noteFacade.findNotesForDashboard(clientId, limit);
        return Response.successResponse(notes);
    }

    @GetMapping(value = "/assessment-statistics")
    public Response<List<ClientAssessmentStatusCountDto>> chartStatistics(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAssessmentFacade.getCountByStatus(clientId));
    }

    @GetMapping(value = "/service-plans/in-development", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientDashboardServicePlanDto> find(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.findInDevelopmentForDashboard(clientId));
    }
}
