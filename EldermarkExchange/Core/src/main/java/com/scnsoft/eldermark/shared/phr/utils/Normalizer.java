package com.scnsoft.eldermark.shared.phr.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author phomal
 * Created on 5/31/2017.
 */
public class Normalizer {

    public static String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("[^0-9]", "");
    }

    public static String normalizeEmail(String email) {
        return StringUtils.lowerCase(email);
    }

    public static String normalizeName(String name) {
        return name == null ? null : StringUtils.lowerCase(name).replaceAll("[' \\-]", "");
    }

}
