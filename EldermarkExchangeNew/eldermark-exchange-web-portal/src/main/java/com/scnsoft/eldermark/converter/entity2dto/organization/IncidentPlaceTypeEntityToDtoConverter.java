package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledValueEntityDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentPlaceType;
import org.springframework.stereotype.Component;

@Component
public class IncidentPlaceTypeEntityToDtoConverter implements ListAndItemConverter<IncidentPlaceType, IdentifiedTitledValueEntityDto<Boolean>> {

    @Override
    public IdentifiedTitledValueEntityDto<Boolean> convert(IncidentPlaceType source) {
        return new IdentifiedTitledValueEntityDto<>(source.getId(),source.getName(),source.getFreeText());
    }

}
