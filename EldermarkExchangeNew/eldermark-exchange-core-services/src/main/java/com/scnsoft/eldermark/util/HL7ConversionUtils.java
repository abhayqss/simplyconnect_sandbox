package com.scnsoft.eldermark.util;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.primitive.CommonDT;
import ca.uhn.hl7v2.model.primitive.CommonTS;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.GregorianCalendar;

public class HL7ConversionUtils {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("UTC"));

    public static String toHL7TSFormat(Instant instant) throws DataTypeException {
        if (instant == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(instant.toEpochMilli());
        return CommonTS.toHl7TSFormat(cal);
    }

    public static String toHL7TSFormatWithSecondsPrecision(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        return dateTimeFormatter.format(temporalAccessor);
    }

    public static String toHL7DTFormat(Instant instant) throws DataTypeException {
        if (instant == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(instant.toEpochMilli());
        return CommonDT.toHl7DTFormat(cal);
    }

    public static String toHL7DTFormat(LocalDate localDate) throws DataTypeException {
        if (localDate == null) {
            return null;
        }
        var cal = (GregorianCalendar) new GregorianCalendar.Builder()
                .setDate(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth())
                .build();
        return CommonDT.toHl7DTFormat(cal);
    }
}
