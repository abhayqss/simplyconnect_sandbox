package com.scnsoft.eldermark.beans.projection;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface NamesAware {
    String getFirstName();
    String getLastName();
    default String getFullName() {
        return Stream.of(getFirstName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
