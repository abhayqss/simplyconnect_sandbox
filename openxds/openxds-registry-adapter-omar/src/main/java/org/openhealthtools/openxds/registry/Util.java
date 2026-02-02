package org.openhealthtools.openxds.registry;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static SimpleDateFormat hl7formatter1 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat hl7formatter2 = new SimpleDateFormat("yyyyMMddHHmm");
    private static SimpleDateFormat hl7formatter3 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat DTMformatter = new SimpleDateFormat("yyyyMMddHHmmssZ");

    public static Date convertHL7Date(String fromDate) {
        if (StringUtils.isBlank(fromDate)) {
            return null;
        } else {
            try {
                if (fromDate.length() == 8) {
                    return hl7formatter1.parse(fromDate);
                } else if (fromDate.length()==12){
                    return  hl7formatter2.parse(fromDate);
                }

                else if (fromDate.length()==14){
                    return  hl7formatter3.parse(fromDate);
                }
                else if (fromDate.length()>14){
                    return  DTMformatter.parse(fromDate);
                }
                else {
                    return  null;
                }
            } catch (ParseException var3) {
                return null;
            }
        }
    }
}
