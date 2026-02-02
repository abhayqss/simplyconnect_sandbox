package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.event.base.ResponsibleManagerConverter;
import com.scnsoft.eldermark.dto.notification.event.PersonNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ResponsibleManagerNotificationConverter extends ResponsibleManagerConverter<PersonNotificationDto> {
    @Override
    protected PersonNotificationDto create() {
        return new PersonNotificationDto();
    }
}
