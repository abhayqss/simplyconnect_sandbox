package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.CoordinatesDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentInjury;
import org.springframework.stereotype.Component;

@Component
public class IncidentInjuryDtoConverter implements ListAndItemConverter<IncidentInjury, CoordinatesDto> {

    @Override
    public CoordinatesDto convert(IncidentInjury source) {
        if (source == null) {
            return null;
        }
        var target = new CoordinatesDto();
        target.setX(source.getX());
        target.setY(source.getY());
        return target;
    }
}
