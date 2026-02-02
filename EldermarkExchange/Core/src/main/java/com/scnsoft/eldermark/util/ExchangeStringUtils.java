package com.scnsoft.eldermark.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ExchangeStringUtils {

    private ExchangeStringUtils() {
    }

    public static String joinNotEmpty(String separator, String... strings) {
        return StringUtils.join(filterEmpty(strings), separator);
    }

    public static String[] filterEmpty(String... strings) {
        final List<String> result = new ArrayList<>();
        for (String string : strings) {
            if (StringUtils.isNotEmpty(string)) {
                result.add(string);
            }
        }
        return result.toArray(new String[0]);
    }

}
