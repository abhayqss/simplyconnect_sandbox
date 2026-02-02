package com.scnsoft.eldermark.entity;

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

}
