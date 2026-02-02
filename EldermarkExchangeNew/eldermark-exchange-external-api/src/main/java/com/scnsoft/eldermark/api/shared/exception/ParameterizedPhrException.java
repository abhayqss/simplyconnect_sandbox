package com.scnsoft.eldermark.api.shared.exception;

/**
 * @author phomal
 */
public class ParameterizedPhrException extends PhrException {

    public ParameterizedPhrException(PhrExceptionType type, Object... params) {
        super(type, String.format(type.message(), params));
    }

}