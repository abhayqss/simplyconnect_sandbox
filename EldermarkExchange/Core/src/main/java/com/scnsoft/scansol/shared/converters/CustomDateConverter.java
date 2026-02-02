package com.scnsoft.scansol.shared.converters;

import org.dozer.DozerConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date: 19.05.15
 * Time: 12:46
 */
public class CustomDateConverter extends DozerConverter<Date, String> {
    public CustomDateConverter() throws Exception {
        this(Date.class, String.class);
    }


    public CustomDateConverter(Class<Date> prototypeA, Class<String> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public String convertTo(Date source, String destination) {
        if(source == null) {
            return null;
        }
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(source);
    }

    @Override
    public Date convertFrom(String source, Date destination) {
        throw new UnsupportedOperationException();
    }
}
