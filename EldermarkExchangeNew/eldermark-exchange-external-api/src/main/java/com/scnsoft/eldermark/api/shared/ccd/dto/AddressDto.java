package com.scnsoft.eldermark.api.shared.ccd.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * This DTO is intended to represent postal addresses (for CCD)
 */
public class AddressDto {

    private String postalCode;

    private String city;

    private String state;

    private String country;

    private String postalAddressUse;

    private String streetAddress;

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalAddressUse() {
        return postalAddressUse;
    }

    public void setPostalAddressUse(String postalAddressUse) {
        this.postalAddressUse = postalAddressUse;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCityStateAndPostalCode() {
        String cityDelimiter = StringUtils.isBlank(city) ? "" : ", ";

    	return StringUtils.defaultIfBlank(city, "") + cityDelimiter +
               StringUtils.defaultIfBlank(state, "") + " " +
               StringUtils.defaultIfBlank(postalCode, "");
    }
    
    public String getCityStatePostalCodeAndCountry() {
        String cityStateAndPostalCode = getCityStateAndPostalCode();
        String delimiter = StringUtils.isBlank(cityStateAndPostalCode) ? "" : ", ";

        return StringUtils.defaultIfBlank(cityStateAndPostalCode, "") + delimiter +
               StringUtils.defaultIfBlank(country, "");
    }
}
