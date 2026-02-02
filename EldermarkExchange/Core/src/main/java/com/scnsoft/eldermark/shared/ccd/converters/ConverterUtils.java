package com.scnsoft.eldermark.shared.ccd.converters;


import org.apache.commons.lang3.StringUtils;

public class ConverterUtils {

    public static String join(String delimeter, String emptyMark, String... args) {
    	String emptyReplaceMark = emptyMark == null ? "?" : emptyMark;
        String prefix = "";

        StringBuilder result = new StringBuilder();
        for (String arg : args) {
            result.append(prefix);
            result.append(StringUtils.isBlank(arg) ? emptyReplaceMark : arg);
            prefix = delimeter;
        }
        return result.toString();
    }
}
