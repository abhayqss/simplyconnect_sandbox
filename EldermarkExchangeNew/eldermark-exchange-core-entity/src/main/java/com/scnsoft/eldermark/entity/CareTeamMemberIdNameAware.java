package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CareTeamMemberIdNameAware {
    Long getEmployeeId();

    String getEmployeeFirstName();

    String getEmployeeLastName();

    default String getFullName() {
        return Stream.of(getEmployeeFirstName(), getEmployeeLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }
}
