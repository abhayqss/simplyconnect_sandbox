package com.scnsoft.eldermark.openxds.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class OpenXdsApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(OpenXdsApiExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public String handleBadRequest(Exception e) {
        logger.error("Exception during request processing", e);
        return e.getMessage();
    }
}

