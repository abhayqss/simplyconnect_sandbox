package com.scnsoft.eldermark.api.shared.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent editable postal addresses (for events).
 *
 * Created by pzhurba on 05-Oct-15.
 */
public class AddressDto {
    private String street;
    private String city;
    private KeyValueDto state;
    private String zip;


    @NotNull
    @Size(max = 255)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @NotNull
    @Size(max = 128)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * USA zip code (5 digits)
     * @return zip
     */
    @NotNull
    @Pattern(regexp = "^\\d{5}$")
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Valid
    @NotNull
    public KeyValueDto getState() {
        return state;
    }

    public void setState(KeyValueDto state) {
        this.state = state;
    }

    /**
     * Full address (computed attribute)
     */
    public String getDisplayAddress() {
        return (getStreet()==null?"":getStreet() + " ") +
                (getCity()==null?"":getCity() + " ") +
                (getState()==null?"":getState().getLabel() + " ") +
                (getZip()==null?"":getZip());
    }

}
