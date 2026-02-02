package com.scnsoft.eldermark.converter;

import com.scnsoft.eldermark.entity.basic.Address;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.scnsoft.eldermark.util.CareCoordinationUtils.concat;

import java.util.Objects;

@Component
public class DisplayAddressConverter implements Converter<Address, String> {

    @Override
    public String convert(Address address) {
        var street = address.getStreetAddress();
        var city = address.getCity();
        var state = address.getState();
        var postalCode = address.getPostalCode();

        return concat(", ", street, city, concat(" ", state, postalCode));
    }
}
