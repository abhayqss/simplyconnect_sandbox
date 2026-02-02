package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.entity.event.EventAddress;
import com.scnsoft.eldermark.event.xml.schema.Address;
import com.scnsoft.eldermark.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventAddressEntityConverter implements Converter<Address, EventAddress> {

    private final StateService stateService;

    @Autowired
    public EventAddressEntityConverter(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public EventAddress convert(Address source) {
        if (source == null) {
            return null;
        }
        var target = new EventAddress();
        target.setCity(source.getCity());
        target.setStreet(source.getStreet());
        target.setZip(source.getZip());
        target.setState(stateService.findByAbbrOrFullName(source.getState(), source.getState()));
        return target;
    }
}
