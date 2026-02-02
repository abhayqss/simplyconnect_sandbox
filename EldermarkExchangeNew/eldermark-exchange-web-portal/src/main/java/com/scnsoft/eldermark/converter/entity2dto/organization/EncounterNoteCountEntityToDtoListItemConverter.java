package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.notes.EntityStatisticsDto;
import com.scnsoft.eldermark.entity.projection.EncounterNoteCount;

@Component
public class EncounterNoteCountEntityToDtoListItemConverter
        implements ListAndItemConverter<EncounterNoteCount, EntityStatisticsDto> {

    @Override
    public EntityStatisticsDto convert(EncounterNoteCount source) {
        EntityStatisticsDto target = new EntityStatisticsDto();
        target.setCount(source.getCount());
        target.setEncounterType(source.getCode().getCode());
        return target;
    }

}
