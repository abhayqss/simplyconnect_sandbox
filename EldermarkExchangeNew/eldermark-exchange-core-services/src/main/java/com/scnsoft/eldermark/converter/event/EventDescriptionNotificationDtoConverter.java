package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.event.base.EventDescriptionViewDataConverter;
import com.scnsoft.eldermark.dto.notification.event.EventDescriptionNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EventDescriptionNotificationDtoConverter extends EventDescriptionViewDataConverter<EventDescriptionNotificationDto> {

    @Override
    protected EventDescriptionNotificationDto create() {
        return new EventDescriptionNotificationDto();
    }

}
