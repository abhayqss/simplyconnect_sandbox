package com.scnsoft.eldermark.dto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AddressDto {

    private Long id;

    @NotEmpty
    @Size(max = 256)
    private String city;

    @NotEmpty
    @Size(max = 256)
    private String street;

    @NotNull
    private Long stateId;
    private String stateName;
    private String stateAbbr;

    @NotNull
    @Pattern(regexp = ValidationRegExpConstants.ZIP_CODE_REGEXP)
    private String zip;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbr) {
        this.stateAbbr = stateAbbr;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDisplayAddress() {
        return Stream.of(getStreet(), getCity(), getStateName(), getZip()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
    }
}
