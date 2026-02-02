package com.scnsoft.eldermark.shared.exception;

/**
 * @author phomal
 */
public class ParameterizedPhrException extends PhrException {

    public ParameterizedPhrException(PhrExceptionType type, Object... params) {
        super(type, String.format(type.message(), params));
    }

}