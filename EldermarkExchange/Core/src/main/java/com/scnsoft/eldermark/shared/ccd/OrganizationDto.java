package com.scnsoft.eldermark.shared.ccd;

import java.util.List;

public class OrganizationDto {
    private String name;

    private List<AddressDto> addresses;

    private TelecomDto telecom;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public TelecomDto getTelecom() {
        return telecom;
    }

    public void setTelecom(TelecomDto telecom) {
        this.telecom = telecom;
    }
}
