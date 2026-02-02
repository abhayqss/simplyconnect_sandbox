package com.scnsoft.eldermark.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.event.EventAddress;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class EventAddressDtoConverter implements Converter<EventAddress, AddressDto> {

    @Override
    public AddressDto convert(EventAddress source) {
        if (source == null) {
            return null;
        }
        var target = new AddressDto();
        target.setCity(source.getCity());
        if (source.getState() != null) {
            target.setStateId(source.getState().getId());
            target.setStateName(source.getState().getName());
        }
        target.setStreet(source.getStreet());
        target.setZip(source.getZip());
        return target;
    }

}
