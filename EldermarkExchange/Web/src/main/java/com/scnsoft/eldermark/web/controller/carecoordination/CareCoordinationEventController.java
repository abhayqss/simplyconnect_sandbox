package com.scnsoft.eldermark.web.controller.carecoordination;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.facades.IncidentFacadeWeb;
import com.scnsoft.eldermark.services.carecoordination.EventNotificationService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventNotificationDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.EventPdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * Created by pzhurba on 21-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/events-log/event/{eventId}")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationEventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventNotificationService eventNotificationService;

    @Autowired
    private IncidentFacadeWeb incidentFacade;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public EventDto getEventDetails(@PathVariable("eventId") Long eventId) {
        return eventService.getEventDetails(eventId);
    }

    @RequestMapping(value = "/event-details", method = RequestMethod.GET)
    public String initEventDetails(@PathVariable("eventId") Long eventId, Model model) {
        eventService.checkAccess(eventId);
        model.addAttribute("eventId", eventId);
        model.addAttribute("event", eventService.getEventDetails(eventId));
        model.addAttribute("canCurrentUserCreateIr", incidentFacade.canCurrentUserCreateIncidentReport(eventId));
        return "event.details";
    }

    @RequestMapping(value = "/page-number", method = RequestMethod.GET)
    @ResponseBody
    public Integer getSentNotifications(@PathVariable("eventId") Long eventId) {
        Set<Long> employeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        return eventService.getPageNumber(eventId,employeeIds);
    }

    @RequestMapping(value = "/event-description", method = RequestMethod.GET)
    public String initEventDescription(@PathVariable("eventId") Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        return "event.description";
    }

    @RequestMapping(value = "/sent-notification", method = RequestMethod.GET)
    public String initEventSentNotification(@PathVariable("eventId") Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        return "event.sent.notifications";
    }

    @RequestMapping(value = "/sent-notification", method = RequestMethod.POST)
    @ResponseBody
    public Page<EventNotificationDto> getSentNotifications(@PathVariable("eventId") Long eventId, Pageable pageRequest) {
        return eventNotificationService.getEventNotifications(eventId, pageRequest, true);
    }

    @RequestMapping(value = "/download-pdf", method = RequestMethod.GET)
    public void downloadPdf(
            @PathVariable("eventId") Long eventId,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        EventDto eventDto = eventService.getEventDetails(eventId);
        ByteArrayOutputStream baos = EventPdfGenerator.generate(eventDto);

        response.setContentType("application/pdf");

        DateFormat df = new SimpleDateFormat("MM-dd-yyyy hh-mm a");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + eventDto.getEventDetails().getEventType()
                + " " + df.format(eventDto.getEventDetails().getEventDatetime()) + ".pdf\"");
        response.setContentLength(baos.size());

        OutputStream os = response.getOutputStream();
        baos.writeTo(os);
        os.flush();
        os.close();  //TODO close in finally block
        baos.close();
    }

}
