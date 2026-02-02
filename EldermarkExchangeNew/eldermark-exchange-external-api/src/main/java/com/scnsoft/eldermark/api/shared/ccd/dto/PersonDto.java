package com.scnsoft.eldermark.api.shared.ccd.dto;

import java.util.List;

public class PersonDto {
    private String code;
    private List<NameDto> names;
    private List<AddressDto> addresses;
    private List<TelecomDto> telecoms;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<NameDto> getNames() {
        return names;
    }

    public void setNames(List<NameDto> names) {
        this.names = names;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<TelecomDto> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<TelecomDto> telecoms) {
        this.telecoms = telecoms;
    }
}
