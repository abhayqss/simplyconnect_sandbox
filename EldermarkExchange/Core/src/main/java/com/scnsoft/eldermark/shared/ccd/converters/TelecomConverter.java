package com.scnsoft.eldermark.shared.ccd.converters;

import com.scnsoft.eldermark.entity.Telecom;
import com.scnsoft.eldermark.shared.ccd.converters.ConverterUtils;
import org.dozer.DozerConverter;

public class TelecomConverter extends DozerConverter<Telecom, String> {

    public TelecomConverter() throws Exception {
        this(Telecom.class, String.class);
    }

    public TelecomConverter(Class<Telecom> prototypeA, Class<String> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public String convertTo(Telecom source, String destination) {
        if (source == null) return null;

        destination = ConverterUtils.join(" ", "?", source.getUseCode(), source.getValue());
        return destination;
    }

    @Override
    public Telecom convertFrom(String source, Telecom destination) {
        throw new UnsupportedOperationException();
    }
}
