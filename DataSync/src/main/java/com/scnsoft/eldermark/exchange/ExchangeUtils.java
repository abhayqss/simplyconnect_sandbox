package com.scnsoft.eldermark.exchange;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExchangeUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

    public static Long replaceZeroByNull(Long value) {
        return value != null && value == 0 ? null : value;
    }

    public static String replace(String str, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }

    public static Date parse4DDate(String dateStr) throws ParseException {
        if(StringUtils.isEmpty(dateStr) || "00/00/0000".equals(dateStr) || "00/00/00".equals(dateStr))
            return null;
        return dateFormat.parse(dateStr.trim());
    }
}
