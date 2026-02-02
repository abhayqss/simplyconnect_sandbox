package com.scnsoft.eldermark.shared.ccd.converters;

import com.scnsoft.eldermark.entity.CcdCode;
import org.dozer.DozerConverter;

public class CcdCodeConverter extends DozerConverter<CcdCode, String> {

    public CcdCodeConverter() throws Exception {
        this(CcdCode.class, String.class);
    }

    public CcdCodeConverter(Class<CcdCode> prototypeA, Class<String> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public String convertTo(CcdCode source, String destination) {
        if(source == null) {
            return null;
        }
        return source.getDisplayName();
    }

    @Override
    public CcdCode convertFrom(String source, CcdCode destination) {
        throw new UnsupportedOperationException();
    }
}
