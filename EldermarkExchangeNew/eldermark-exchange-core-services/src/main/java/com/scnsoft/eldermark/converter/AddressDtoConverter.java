package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoConverter implements Converter<Address, AddressDto> {

    @Autowired
    private StateService stateService;

    @Override
    public AddressDto convert(Address source) {
        AddressDto dto = new AddressDto();
        dto.setId(source.getId());
        dto.setCity(source.getCity());
        dto.setStreet(source.getStreetAddress());
        if (source.getState() != null) {
            State state = stateService.findByAbbr(source.getState());
            if (state != null) {
                dto.setStateName(state.getName());
                dto.setStateId(state.getId());
                dto.setStateAbbr(state.getAbbr());
            }
        }
        dto.setZip(source.getPostalCode());
        return dto;
    }

    public AddressDto fromAddressFields(State state, String city, String streetAddress, String zip) {
        AddressDto dto = new AddressDto();
        dto.setCity(city);
        dto.setStreet(streetAddress);
        dto.setZip(zip);
        if (state != null) {
            dto.setStateName(state.getName());
            dto.setStateId(state.getId());
            dto.setStateAbbr(state.getAbbr());
        }
        return dto;
    }
}
