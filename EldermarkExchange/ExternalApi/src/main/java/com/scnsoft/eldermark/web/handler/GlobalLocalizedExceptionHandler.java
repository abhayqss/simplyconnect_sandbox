package com.scnsoft.eldermark.web.handler;

import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exception.SqlConstraintMsgMappingResolver;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.web.entity.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * @author phomal
 * Created by phomal on 1/20/2018.
 */
@ControllerAdvice
@ResponseBody
public class GlobalLocalizedExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLocalizedExceptionHandler.class);

    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Response<String> handle(EntityNotFoundException ex) {
        ex.printStackTrace();
        return Response.errorResponse(ex.getMessage(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Handle SQL Server constraints violation
     */
    @ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<String> handle(DataIntegrityViolationException ex) {
        ex.printStackTrace();

        // default exception
        PhrException phrException = new PhrException(PhrException.DATA_INTEGRITY_VIOLATION_CODE,
                ex.getMostSpecificCause().getClass().getSimpleName() + ": " + ex.getMostSpecificCause().getMessage());

        // extract the affected database constraint name
        if ((ex.getCause() != null) && (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
            String constraintName = ((org.hibernate.exception.ConstraintViolationException) ex.getCause()).getConstraintName();
            PhrExceptionType phrExceptionType = SqlConstraintMsgMappingResolver.map(constraintName);
            if (phrExceptionType != null) {
                phrException = new PhrException(phrExceptionType);
            }
        }

        return Response.errorResponse(phrException, HttpStatus.SC_CONFLICT);
    }

    /**
     * Handle Form Input Validation constraints violation
     */
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> handle(ConstraintViolationException ex) {
        ex.printStackTrace();
        PhrException phrException = ValidationExceptionFactory.fromConstraintViolation(ex, true);
        return Response.errorResponse(phrException, phrException.getHttpStatus());
    }

    @ExceptionHandler(PhrException.class)
    public Response<String> handle(PhrException ex, HttpServletResponse response) {
        if (PhrExceptionType.AVATAR_NOT_FOUND.code().equals(ex.getCode())) {
            logger.warn(ex.getMessage());
        } else {
            ex.printStackTrace();
        }
        response.setStatus(ex.getHttpStatus());
        return Response.errorResponse(ex, ex.getHttpStatus());
    }

    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<String> handle(Exception ex) {
        ex.printStackTrace();
        return Response.errorResponse(ex.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
