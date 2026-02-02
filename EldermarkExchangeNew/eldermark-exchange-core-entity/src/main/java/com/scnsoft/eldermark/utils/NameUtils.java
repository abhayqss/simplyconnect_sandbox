package com.scnsoft.eldermark.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class NameUtils {

    private NameUtils() {
    }

    public static String getFullName(String firstName, String lastName) {
        return Stream.of(firstName, lastName)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.joining(" "));
    }
}
