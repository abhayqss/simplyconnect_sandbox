package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.Client;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ClientUtils {

    private ClientUtils() {
    }

    public static String getInitials(Client client, String delimiter) {
        var firstName = client.getFirstName();
        var lastName = client.getLastName();

        return getInitials(firstName, lastName, delimiter);
    }

    public static String getInitials(String firstName, String lastName, String delimiter) {
        return Stream.of(firstName, lastName)
                .filter(StringUtils::isNotEmpty)
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining(delimiter));
    }

    public static String formatSsn(String ssnLast4) {
        return StringUtils.isNotBlank(ssnLast4) ? String.format("***-**-%s", ssnLast4) : ssnLast4;
    }

}
