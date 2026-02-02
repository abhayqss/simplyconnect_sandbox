package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaAddressDto {

    private String use;
    private List<String> line;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public ConsanaAddressDto(String use, List<String> line, String city, String state, String postalCode, String country) {
        this.use = use;
        this.line = line;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public List<String> getLine() {
        return line;
    }

    public void setLine(List<String> line) {
        this.line = line;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
