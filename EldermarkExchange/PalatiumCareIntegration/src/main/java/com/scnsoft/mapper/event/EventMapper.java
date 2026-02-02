package com.scnsoft.mapper.event;

import com.scnsoft.dto.incoming.PalCareEventDto;
import com.scnsoft.eldermark.entity.palatiumcare.PCEvent;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import org.modelmapper.convention.MatchingStrategies;

public class EventMapper extends GenericMapper<PCEvent, PalCareEventDto> {

    {
        getModelMapper().getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        getModelMapper().addMappings(new EventDtoToEventMap());
    }

    @Override
    protected Class<PCEvent> getEntityClass() {
        return PCEvent.class;
    }

    @Override
    protected Class<PalCareEventDto> getDtoClass() {
        return PalCareEventDto.class;
    }

}
