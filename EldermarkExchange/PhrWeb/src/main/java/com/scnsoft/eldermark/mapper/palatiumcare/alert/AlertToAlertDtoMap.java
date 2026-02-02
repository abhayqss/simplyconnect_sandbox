package com.scnsoft.eldermark.mapper.palatiumcare.alert;

import com.scnsoft.eldermark.entity.palatiumcare.Alert;
import com.scnsoft.eldermark.shared.palatiumcare.AlertDto;
import org.modelmapper.PropertyMap;

public class AlertToAlertDtoMap extends PropertyMap<Alert, AlertDto> {

    @Override
    protected void configure() {
        map(source.getEvent().getAckDateTime(), destination.getAckDateTime());
        map(source.getEvent().getEventDateTime(), destination.getEventDateTime());
        map(source.getEvent().getResident(), destination.getResident());
        map(source.getEvent().getDeviceType().getName(), destination.getDevice().getName());
        map(source.getEvent().getResident().getId(), destination.getResident().getId());
        map(source.getEvent().getResident().getLocation(), destination.getResident().getLocation());
        map(source.getStatus(), destination.getStatus());
        map(source.getAlertType(), destination.getAlertType());
    }
}

