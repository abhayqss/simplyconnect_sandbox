package com.scnsoft.eldermark.services.hl7.util;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.primitive.CommonDT;
import ca.uhn.hl7v2.model.primitive.CommonTS;
import com.scnsoft.eldermark.entity.xds.datatype.CodedValueForHL7Table;

import java.util.Date;
import java.util.GregorianCalendar;

public class Hl7Utils {

    public static <T> T getNullCheckedResult(MessageProperty<T> property) {
        try {
            return property.getProperty();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public static String toHl7TSFormat(Date date) throws DataTypeException {
        if (date == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return CommonTS.toHl7TSFormat(cal);
    }

    public static String toHl7DTFormat(Date date) throws DataTypeException {
        if (date == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return CommonDT.toHl7DTFormat(cal);
    }


    public static String getRawCode(CodedValueForHL7Table codedValue) {
        if (codedValue == null) {
            return null;
        }
        return codedValue.getRawCode();
    }
}
