package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.schema.DeviceEvents;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.EventsLogService;
import com.scnsoft.eldermark.shared.carecoordination.events.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.web.*;
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
import java.util.ArrayList;
import java.util.List;

//@RequestMapping("/device-events")
//@Controller
public class DeviceEventsController {
    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    @Value("classpath:${events.xsd.file.device}")
    Resource xsd;

    @Autowired
    private EventService eventService;
    @Autowired
    private EventsLogService eventsLogService;

    private final Unmarshaller deviceEventsUnmarshaller;

    @Autowired
    public DeviceEventsController(@Qualifier("deviceEventsUnmarshaller") Unmarshaller deviceEventsUnmarshaller) {
        this.deviceEventsUnmarshaller = deviceEventsUnmarshaller;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, consumes = {"application/xml","text/xml"})
    @ResponseBody
    public DeviceEventsResponse post(HttpServletRequest request) throws Exception {
        final Long now = System.currentTimeMillis();
        final String body = IOUtils.toString(request.getInputStream());

        logger.info("New Device Events come to the system: \n" + body + "\n Current Thread : " + Thread.currentThread().getId());

        eventsLogService.logIncomingMessage(request, body);
        final DeviceEvents events = unmarshalDeviceEvent(body);
        DeviceEventProcessingResultDto result = eventService.processDeviceEvents(events);

        logger.info("finish processing XML, takes " + (System.currentTimeMillis() - now) + "ms.");
        return createResponse(0,"New event was created", "Patient not found", result);
    }

    private DeviceEvents unmarshalDeviceEvent(String xml) throws JAXBException {
        synchronized (deviceEventsUnmarshaller) {
            return (DeviceEvents) deviceEventsUnmarshaller.unmarshal(new StringReader(xml));
        }
    }

    //TODO move to superclass for Events and DeviceEvents controllers
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

    private DeviceEventsResponse createResponse(int code, String successMessage, String failureMessage, DeviceEventProcessingResultDto resultDto) {
        final DeviceEventsResponse response = new DeviceEventsResponse();
        response.setCode(code);
        List<DeviceEventResponseDetails> detailsList = new ArrayList<>();
        for (String deviceId : resultDto.getProcessed()) {
            DeviceEventResponseDetails responseDetails = createDeviceEventResponseDetails(successMessage, deviceId);
            detailsList.add(responseDetails);
        }
        for (String deviceId : resultDto.getFailed()) {
            DeviceEventResponseDetails responseDetails = createDeviceEventResponseDetails(failureMessage, deviceId);
            detailsList.add(responseDetails);
        }
        response.setDetails(detailsList);
        return response;
    }

    private DeviceEventResponseDetails createDeviceEventResponseDetails(String successMessage, String deviceId) {
        DeviceEventResponseDetails responseDetails = new DeviceEventResponseDetails();
        responseDetails.setDeviceId(deviceId);
        responseDetails.setMessage(successMessage);
        return responseDetails;
    }
}
