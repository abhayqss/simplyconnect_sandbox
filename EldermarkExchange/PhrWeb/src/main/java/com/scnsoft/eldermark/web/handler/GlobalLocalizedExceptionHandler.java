package com.scnsoft.eldermark.web.handler;

import com.scnsoft.eldermark.services.exceptions.TimedLockedException;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.exception.SqlConstraintMsgMappingResolver;
import com.scnsoft.eldermark.shared.exception.ValidationExceptionFactory;
import com.scnsoft.eldermark.shared.web.entity.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/3/2017.
 */
@ControllerAdvice
@ResponseBody
public class GlobalLocalizedExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLocalizedExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public Response<String> handle(BadCredentialsException ex) {
        ex.printStackTrace();
        return Response.errorResponse(PhrExceptionType.BAD_CREDENTIALS);
    }

    @ExceptionHandler(TimedLockedException.class)
    public Response<String> handle(TimedLockedException ex) {
        ex.printStackTrace();
        return Response.errorResponse(new PhrException(PhrExceptionType.ACCOUNT_IS_LOCKED_OUT, ex.getMessage()), HttpStatus.SC_UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Response<String> handle(EntityNotFoundException ex) {
        ex.printStackTrace();
        return Response.errorResponse(ex.getMessage(), HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Handle SQL Server constraints violation
     */
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
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<String> handle(ConstraintViolationException ex) {
        ex.printStackTrace();
        PhrException phrException = ValidationExceptionFactory.fromConstraintViolation(ex, false);
        return Response.errorResponse(phrException, phrException.getHttpStatus());
    }

    @ExceptionHandler(PhrException.class)
    public Response<String> handle(PhrException ex) {
        if (PhrExceptionType.AVATAR_NOT_FOUND.code().equals(ex.getCode())) {
            logger.warn(ex.getMessage());
        } else {
            ex.printStackTrace();
        }
        return Response.errorResponse(ex, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public Response<String> handle(Exception ex) {
        ex.printStackTrace();
        return Response.errorResponse(ex.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
