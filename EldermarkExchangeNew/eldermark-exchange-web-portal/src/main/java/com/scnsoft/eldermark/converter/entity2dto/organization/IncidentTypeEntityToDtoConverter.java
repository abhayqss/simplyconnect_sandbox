package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.IncidentTypeDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
