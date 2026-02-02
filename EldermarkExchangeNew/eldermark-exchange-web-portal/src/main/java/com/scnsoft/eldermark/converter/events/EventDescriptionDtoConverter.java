package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.EventDescriptionViewDataConverter;
import com.scnsoft.eldermark.dto.events.EventDescriptionDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EventDescriptionDtoConverter extends EventDescriptionViewDataConverter<EventDescriptionDto> {

    @Override
    protected EventDescriptionDto create() {
        return new EventDescriptionDto();
    }

}
