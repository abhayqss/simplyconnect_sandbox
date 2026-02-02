package com.scnsoft.eldermark.entity.basic;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Address {
    Long getId();

    String getPostalAddressUse();

    String getStreetAddress();

    String getCity();

    String getState();

    String getCountry();

    String getPostalCode();

    void setPostalAddressUse(String postalAddressUse);

    void setStreetAddress(String streetAddress);

    void setCity(String city);

    void setState(String state);

    void setCountry(String country);

    void setPostalCode(String postalCode);

    default String getDisplayAddress(String separator) {
        return Stream.of(getStreetAddress(), getCity(), getState(), getPostalCode()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(separator));
    }

}
