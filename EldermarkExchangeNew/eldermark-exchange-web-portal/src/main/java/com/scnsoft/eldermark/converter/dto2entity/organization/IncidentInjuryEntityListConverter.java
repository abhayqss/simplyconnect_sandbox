package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.CoordinatesDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentInjury;
import org.springframework.stereotype.Component;

@Component
public class IncidentInjuryEntityListConverter extends IncidentReportEntityListConverter<CoordinatesDto, IncidentInjury> {

    @Override
    public IncidentInjury convert(CoordinatesDto source) {
        var target = new IncidentInjury();
        target.setX(source.getX());
        target.setY(source.getY());
        return target;
    }
}
