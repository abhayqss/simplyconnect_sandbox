package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ConsanaOrganizationDto {

    private List<ConsanaIdentifierDto> identifier;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ConsanaContactPointDto> telecom;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ConsanaAddressDto> address;

    public List<ConsanaIdentifierDto> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<ConsanaIdentifierDto> identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConsanaContactPointDto> getTelecom() {
        return telecom;
    }

    public void setTelecom(List<ConsanaContactPointDto> telecom) {
        this.telecom = telecom;
    }

    public List<ConsanaAddressDto> getAddress() {
        return address;
    }

    public void setAddress(List<ConsanaAddressDto> address) {
        this.address = address;
    }
}
