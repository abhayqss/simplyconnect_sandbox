package com.scnsoft.eldermark.converter.dictionary;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.dictionary.FreeTextKeyValueDto;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;

@Component
public class IncidentPlaceTypeEntityToDtoConverter extends ListAndItemTransformer<IncidentPlaceType, FreeTextKeyValueDto>{

    @Override
    public FreeTextKeyValueDto convert(IncidentPlaceType source) {
        return new FreeTextKeyValueDto(source.getId(),source.getName(),source.getFreeText());
    }

}
