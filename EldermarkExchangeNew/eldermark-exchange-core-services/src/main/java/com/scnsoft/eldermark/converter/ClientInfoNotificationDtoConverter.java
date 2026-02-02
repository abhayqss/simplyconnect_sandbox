package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.converter.event.base.BaseClientSummaryViewDataConverter;
import com.scnsoft.eldermark.dto.notification.event.ClientInfoNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientInfoNotificationDtoConverter extends BaseClientSummaryViewDataConverter<ClientInfoNotificationDto> {

    @Override
    protected ClientInfoNotificationDto create() {
        return new ClientInfoNotificationDto();
    }
}
