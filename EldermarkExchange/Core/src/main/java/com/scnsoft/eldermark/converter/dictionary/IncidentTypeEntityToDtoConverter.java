package com.scnsoft.eldermark.converter.dictionary;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.dictionary.IncidentTypeDto;
import com.scnsoft.eldermark.entity.incident.IncidentType;

@Component
public class IncidentTypeEntityToDtoConverter implements Converter<IncidentType, IncidentTypeDto> {

    @Override
    public IncidentTypeDto convert(IncidentType incidentType) {
        IncidentTypeDto result = new IncidentTypeDto();
        result.setId(incidentType.getId());
        result.setLevel(incidentType.getIncidentLevel());
        result.setTitle(incidentType.getName());
        result.setIsFreeText(incidentType.getFreeText());
        return result;
    }
}
