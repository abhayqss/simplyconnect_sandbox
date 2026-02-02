package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.EventEssentialsViewDataConverter;
import com.scnsoft.eldermark.dto.events.EventEssentialsDto;
import com.scnsoft.eldermark.entity.event.Event;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EventEssentialsDtoConverter extends EventEssentialsViewDataConverter<EventEssentialsDto> {

    @Override
    protected EventEssentialsDto create() {
        return new EventEssentialsDto();
    }

    @Override
    protected void fill(Event event, EventEssentialsDto essentials) {
        super.fill(event, essentials);

        essentials.setTypeId(event.getEventType().getId());
        essentials.setTypeName(event.getEventType().getCode());
    }
}
