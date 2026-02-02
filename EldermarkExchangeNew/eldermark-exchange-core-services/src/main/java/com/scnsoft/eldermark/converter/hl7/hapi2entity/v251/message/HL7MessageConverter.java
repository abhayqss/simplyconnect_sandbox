package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.message;

import ca.uhn.hl7v2.model.Message;
import org.springframework.core.convert.converter.Converter;

public abstract class HL7MessageConverter<S extends Message, T> implements Converter<S, T> {

    public T convert(S source) {
        return isMessageEmpty(source) ? null : doConvert(source);
    }

    protected boolean isMessageEmpty(S source) {
        return source == null;
    }

    protected abstract T doConvert(S source);

}
