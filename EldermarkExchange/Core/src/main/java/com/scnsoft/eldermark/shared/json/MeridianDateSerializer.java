package com.scnsoft.eldermark.shared.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeridianDateSerializer extends JsonSerializer<Date> {
    public static final String EXCHANGE_DATE_FORMAT = "MM/dd/yyyy hh:mm a";

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws
            IOException {
        SimpleDateFormat formatter = new SimpleDateFormat(EXCHANGE_DATE_FORMAT);
        String formattedDate = formatter.format(value);
        formattedDate = formattedDate.replace("AM", "am").replace("PM", "pm");
        gen.writeString(formattedDate);
    }
}
