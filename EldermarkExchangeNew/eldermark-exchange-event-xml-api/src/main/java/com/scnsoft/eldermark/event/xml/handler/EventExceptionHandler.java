package com.scnsoft.eldermark.event.xml.handler;

import com.scnsoft.eldermark.event.xml.response.EventResponseStatus;
import com.scnsoft.eldermark.event.xml.response.EventsErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.rmi.UnmarshalException;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
public class EventExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UnmarshalException.class})
    @ResponseBody
    public EventsErrorResponse handleBadRequest(UnmarshalException e) {
        logger.info("Bad request", e);
        return new EventsErrorResponse(new EventResponseStatus(100, e.getCause().getMessage()));
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseBody
    public EventsErrorResponse handleNotSupported(HttpMediaTypeNotSupportedException e) {
        logger.info("Media Type Not Supported", e);
        return new EventsErrorResponse(new EventResponseStatus(415, e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public EventsErrorResponse handleBadRequest(Exception e) {
        logger.info("Internal Server error", e);
        return new EventsErrorResponse(new EventResponseStatus(101, e.getMessage()));
    }
}
