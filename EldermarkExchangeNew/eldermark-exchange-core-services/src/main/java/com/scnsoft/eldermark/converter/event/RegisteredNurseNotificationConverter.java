package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.event.base.RegisteredNurseConverter;
import com.scnsoft.eldermark.dto.notification.event.PersonNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class RegisteredNurseNotificationConverter extends RegisteredNurseConverter<PersonNotificationDto> {

    @Override
    protected PersonNotificationDto create() {
        return new PersonNotificationDto();
    }

}
