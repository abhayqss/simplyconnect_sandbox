package com.scnsoft.eldermark.web.controller.handler;

import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.DocumentAlreadyStoredException;
import com.scnsoft.eldermark.shared.exceptions.LocalizedException;
import com.scnsoft.eldermark.web.exception.BadRequestException;
import com.scnsoft.eldermark.web.exception.TokenNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.ws.soap.SOAPFaultException;

@ControllerAdvice
public class GlobalLocalizedExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLocalizedExceptionHandler.class);

    @ExceptionHandler(LocalizedException.class)
    @ResponseBody
    public ResponseEntity<String> handle(LocalizedException ex) {
        logger.error("LocalizedException was caught by a GlobalLocalizedExceptionHandler", ex);

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<String>(ex.getLocalizedMessage(), httpStatus);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handle(BusinessException e) {
        logger.warn("BusinessException was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handle(BadRequestException e) {
        logger.warn("BadRequestException was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SOAPFaultException.class)
    public ResponseEntity<String> handle(SOAPFaultException e) {
        logger.error("SOAPFaultException was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DocumentAlreadyStoredException.class)
    public ResponseEntity<String> handle(DocumentAlreadyStoredException e) {
        logger.error("Exception was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleMediaNotSupported(Exception e) {
        logger.error("Exception was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(BusinessAccessDeniedException.class)
    public ResponseEntity<String> handle(BusinessAccessDeniedException e) {
        logger.error("Exception was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handle(AccessDeniedException e) {
        logger.info(e.getMessage(), e);
        ModelAndView model = new ModelAndView();
        model.addObject("errMsg", "There is no access to system data for logged user. Please contact your administrator for more details");
        model.setViewName("care.coordination.error");
        return model;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        logger.error("Exception was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handle(Throwable e) {
        logger.error("Throwable was caught by a GlobalLocalizedExceptionHandler", e);
        return new ResponseEntity<String>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenNotExistsException.class)
    public ModelAndView handle(TokenNotExistsException e) {
        logger.info(e.getMessage(), e);
        ModelAndView model = new ModelAndView();
        model.addObject("errMsg", e.getMessage());
        model.setViewName("care.coordination.error");
        return model;
    }
}
