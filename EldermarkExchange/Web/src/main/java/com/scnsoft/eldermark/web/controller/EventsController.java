package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facades.AdtToCcdDataConversionFacade;
import com.scnsoft.eldermark.schema.Events;
import com.scnsoft.eldermark.services.OutboundAdtService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.EventsLogService;
import com.scnsoft.eldermark.shared.carecoordination.AdtDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.web.EventResponseStatus;
import com.scnsoft.eldermark.web.EventsErrorResponse;
import com.scnsoft.eldermark.web.EventsResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Date;

/**
 * @author averazub
 * @author knetkachou
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 21-Sep-15.
 */
//@RequestMapping("/events")
//@Controller
public class EventsController {
    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Value("classpath:${events.xsd.file}")
    Resource xsd;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventsLogService eventsLogService;

    @Autowired
    private OutboundAdtService outboundAdtService;

    @Autowired
    private AdtToCcdDataConversionFacade adtToCcdDataConversionFacade;

    private final Unmarshaller eventsUnmarshaller;

    @Autowired
    public EventsController(@Qualifier("eventsUnmarshaler") Unmarshaller eventsUnmarshaller) {
        this.eventsUnmarshaller = eventsUnmarshaller;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/xml", "text/xml"})
    @ResponseBody
    public EventsResponse post(HttpServletRequest request) throws Exception {
        final Long now = System.currentTimeMillis();
        final String body = IOUtils.toString(request.getInputStream());

        logger.info("New Events come to the system: \n" + body + "\n Current Thread : " + Thread.currentThread().getId());

        eventsLogService.logIncomingMessage(request, body);
        final Events events = unmarshal(body);
        eventService.processEvents(events);

        logger.info("finish processing XML, takes " + (System.currentTimeMillis() - now) + "ms.");
        return createResponse(0, "New Events was created");
    }

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET, value = "/{eventId:\\d+}")
    public EventDto getEvent(@PathVariable("eventId") Long eventId) {
        return eventService.getEventDetails(eventId);
    }

    private Events unmarshal(String xml) throws JAXBException {
        synchronized (eventsUnmarshaller) {
            return (Events) eventsUnmarshaller.unmarshal(new StringReader(xml));
        }
    }


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/adt", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public EventsResponse postAdtEvent(
            @RequestParam(value = "residentId") Long residentId,
            @RequestParam(value = "adtType", required = false) Long adtType,
            @RequestParam(value = "new", required = false) Boolean newPatient,
            @RequestParam(value = "msgId", required = false) Long msgId,
            @RequestParam(value = "databaseOid") String databaseOid) {
        logger.info("ADT Message Came to the System. Generate Encounter-ADT event. Message type: " + (adtType == null ? "" : adtType));
        AdtDto adtDto = new AdtDto();
        adtDto.setResidentId(residentId);
        adtDto.setEventDate(new Date());
        adtDto.setMsgId(msgId);
        adtDto.setNewPatient(newPatient);
        adtDto.setDatabaseOid(databaseOid);
        logger.info("AdtDto data: {}", adtDto);
        eventService.processAdtEvent(adtDto, adtType);
        adtToCcdDataConversionFacade.convertAndSave(residentId, msgId, adtDto.getEventDate());
        return createResponse(0, "New Encounter-ADT Event was created");
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/send-adt-test", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public void testAdt(
//            @RequestParam(value = "residentId") Long residentId,
//            @RequestParam(value = "adtType", required = false) Long adtType,
//            @RequestParam(value = "new", required = false) Boolean newPatient,
            @RequestParam(value = "msgId", required = false) Long msgId
//            @RequestParam(value = "databaseOid") String databaseOid)
    ) {
        logger.info("Test ADT endpoint is triggered for message id {}", msgId);
        outboundAdtService.sendOutAdts(msgId);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping("unauthorised")
    @ResponseBody
    public EventsErrorResponse unAuthorised() {
        logger.info("Unauthorised access");
        return createErrorResponse(102, "Unauthorised Request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnmarshalException.class)
    @ResponseBody
    public EventsErrorResponse handleBadRequest(final UnmarshalException e) {
        logger.info("Bad request", e);
        return createErrorResponse(100, e.getCause().getMessage());
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public EventsErrorResponse handleNotSupported(final HttpMediaTypeNotSupportedException e) {
        logger.info("Media Type Not Supported", e);
        return createErrorResponse(415, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public EventsErrorResponse handleBadRequest(final Exception e) {
        logger.info("Internal Server error", e);
        return createErrorResponse(101, e.getMessage());
    }

    private EventsErrorResponse createErrorResponse(int code, String message) {
        final EventsErrorResponse response = new EventsErrorResponse();
        final EventResponseStatus status = new EventResponseStatus();

        status.setCode(code);
        status.setDetails(message);

        response.setStatus(status);
        return response;
    }

    private EventsResponse createResponse(int code, String message) {
        final EventsResponse response = new EventsResponse();
        final EventResponseStatus status = new EventResponseStatus();

        status.setCode(code);
        status.setDetails(message);

        response.setStatus(status);
        return response;
    }

}
