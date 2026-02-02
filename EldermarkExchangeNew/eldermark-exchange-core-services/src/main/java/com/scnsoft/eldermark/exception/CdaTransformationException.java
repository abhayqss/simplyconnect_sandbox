package com.scnsoft.eldermark.exception;

public class CdaTransformationException extends InternalServerException {

    public CdaTransformationException(Throwable cause) {
        super(InternalServerExceptionType.CDA_TRANSFORMATION_EXCEPTION, cause);
    }
}
