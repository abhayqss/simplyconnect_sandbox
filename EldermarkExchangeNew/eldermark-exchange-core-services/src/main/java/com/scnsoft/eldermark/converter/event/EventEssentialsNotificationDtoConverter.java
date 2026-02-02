package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.event.base.EventEssentialsViewDataConverter;
import com.scnsoft.eldermark.dto.notification.event.EventEssentialsNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EventEssentialsNotificationDtoConverter extends EventEssentialsViewDataConverter<EventEssentialsNotificationDto> {

    @Override
    protected EventEssentialsNotificationDto create() {
        return new EventEssentialsNotificationDto();
    }

}
