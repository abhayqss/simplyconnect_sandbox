package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportSetter;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class IncidentReportEntityListConverter<S, T extends IncidentReportSetter> implements Converter<S, T> {

    public List<T> convertList(List<S> sourceList, IncidentReport incidentReport) {
        return Optional.ofNullable(sourceList)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .peek(i -> i.setIncidentReport(incidentReport))
                .collect(Collectors.toList());
    }
}
