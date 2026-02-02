package com.scnsoft.eldermark.exception.cda;

import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;

public class CdaParsingException extends InternalServerException {

    public CdaParsingException(String message) {
        super(InternalServerExceptionType.CDA_PARSING_ERROR, message);
    }
}
