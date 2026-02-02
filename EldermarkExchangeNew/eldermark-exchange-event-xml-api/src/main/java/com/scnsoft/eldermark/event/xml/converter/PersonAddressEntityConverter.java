package com.scnsoft.eldermark.event.xml.converter;

import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.event.xml.schema.Address;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonAddressEntityConverter implements Converter<Address, PersonAddress> {

    private final StateService stateService;

    @Autowired
    public PersonAddressEntityConverter(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public PersonAddress convert(Address address) {
        if (address == null) {
            return null;
        }
        var personAddress = CareCoordinationUtils.createAddress();
        personAddress.setCity(address.getCity());
        personAddress.setStreetAddress(address.getStreet());
        personAddress.setState(Optional.ofNullable(stateService.findByAbbrOrFullName(address.getState(), address.getState()))
                .map(State::getAbbr)
                .orElse(null));
        personAddress.setPostalCode(address.getZip());
        return personAddress;
    }
}
