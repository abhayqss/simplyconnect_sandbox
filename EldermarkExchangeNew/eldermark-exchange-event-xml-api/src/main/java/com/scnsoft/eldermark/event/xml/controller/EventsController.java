package com.scnsoft.eldermark.event.xml.controller;

import com.scnsoft.eldermark.event.xml.facade.EventsFacade;
import com.scnsoft.eldermark.event.xml.response.EventResponseStatus;
import com.scnsoft.eldermark.event.xml.response.EventsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/events")
public class EventsController {

    private static final Logger logger = LoggerFactory.getLogger(EventsController.class);

    private final EventsFacade eventsFacade;

    @Autowired
    public EventsController(EventsFacade eventsFacade) {
        this.eventsFacade = eventsFacade;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public EventsResponse receiveEvents(HttpServletRequest request) throws Exception {
        logger.info("New Events come to the system");
        eventsFacade.processEvents(request);
        return new EventsResponse(new EventResponseStatus(0, "New Events was created"));
    }
}
