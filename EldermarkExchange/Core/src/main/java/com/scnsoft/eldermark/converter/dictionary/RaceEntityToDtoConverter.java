package com.scnsoft.eldermark.converter.dictionary;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.entity.incident.Race;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

@Component
public class RaceEntityToDtoConverter extends ListAndItemTransformer<Race, KeyValueDto>{

    @Override
    public KeyValueDto convert(Race source) {
        return new KeyValueDto(source.getId(),source.getName());
    }

}
