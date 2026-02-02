package com.scnsoft.eldermark.consana.sync.client.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsanaUtils {

    public static String getFullName(String firstName, String lastName) {
        return concat(" ", firstName, lastName);
    }

    public static String concat(String delimiter, String... args) {
        return Stream.of(args).filter(StringUtils::isNotEmpty).collect(Collectors.joining(delimiter));
    }

}
