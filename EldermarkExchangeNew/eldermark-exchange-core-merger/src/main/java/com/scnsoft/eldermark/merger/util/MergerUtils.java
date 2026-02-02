package com.scnsoft.eldermark.merger.util;

import java.util.Optional;

public final class MergerUtils {

    private MergerUtils(){}

    public static String normalizeSsn(String ssn) {
        return Optional.ofNullable(ssn)
                .map(s -> s.toLowerCase().replaceAll("[' \\-+()]", ""))
                .map(String::trim)
                .orElse(null);
    }

    public static String normalizeString(String str) {
        return Optional.ofNullable(str)
                .map(String::toLowerCase)
                .map(String::trim)
                .orElse(null);
    }

    public static String normalizeStreet(String str) {
        return Optional.ofNullable(str)
                .map(it -> it.replaceAll("[^a-z A-Z0-9]", ""))
                .map(String::toLowerCase)
                .map(String::trim)
                .orElse(null);
    }
}
