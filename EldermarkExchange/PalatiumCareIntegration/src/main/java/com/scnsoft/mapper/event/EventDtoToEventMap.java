package com.scnsoft.mapper.event;


import com.scnsoft.dto.incoming.PalCareEventDto;
import com.scnsoft.eldermark.entity.palatiumcare.PCEvent;
import org.modelmapper.PropertyMap;

public class EventDtoToEventMap extends PropertyMap<PalCareEventDto, PCEvent> {

    @Override
    protected void configure() {
        map(source.getId(), destination.getPalCareId());
        map(source.getAckDateTime(), destination.getAckDateTime());
        map(source.getEventDateTime(), destination.getEventDateTime());
        map(source.getDevice().getType(), destination.getDeviceType().getName());
        map(source.getLocation(), destination.getResident().getLocation());
    }
}

