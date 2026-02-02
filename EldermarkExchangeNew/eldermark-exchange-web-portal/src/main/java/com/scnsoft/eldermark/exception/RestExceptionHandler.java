package com.scnsoft.eldermark.exception;

import com.scnsoft.eldermark.web.commons.dto.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({BusinessException.class})
    protected ResponseEntity<Response> handleBusinessException(BusinessException ex) {
        logger.error("Uncaught exception", ex);
        return new ResponseEntity<>(Response.errorResponse(ex), HttpStatus.OK);
    }

    @ExceptionHandler({InternalServerException.class})
    protected ResponseEntity<Response> handleInternalServerException(InternalServerException ex) {
        logger.error("Uncaught exception", ex);
        return new ResponseEntity<>(Response.errorResponse(ex), ex.getHttpStatus());
    }

    @ExceptionHandler({ApplicationException.class})
    protected ResponseEntity<Response> handleApplicationException(ApplicationException ex) {
        logger.error("Uncaught exception", ex);
        return new ResponseEntity<>(Response.errorResponse(
                new InternalServerException(ex.getMessage(), ex.getCode(), ex.getCause(), HttpStatus.INTERNAL_SERVER_ERROR)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access Denied exception", ex);
        return new ResponseEntity<>(Response.errorResponse(new InternalServerException(InternalServerExceptionType.ACCESS_DENIED))
                , InternalServerExceptionType.ACCESS_DENIED.getHttpStatus());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Response> handleBaseException(Exception ex) {
        var errorUuid = UUID.randomUUID().toString();
        logger.error("Uncaught exception, uuid={}", errorUuid, ex);
        String message = "Internal Server Error " + errorUuid;
        return new ResponseEntity<>(Response.errorResponse(new InternalServerException(message, "internal.server.error", ex.getCause(), HttpStatus.INTERNAL_SERVER_ERROR))
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
