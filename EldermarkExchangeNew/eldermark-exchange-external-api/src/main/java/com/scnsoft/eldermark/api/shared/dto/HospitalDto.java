package com.scnsoft.eldermark.api.shared.dto;

/**
 * Created by pzhurba on 05-Oct-15.
 */
public class HospitalDto implements WithAddressDto{
    private String name;
    private boolean includeAddress;
    private AddressDto address;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(boolean includeAddress) {
        this.includeAddress = includeAddress;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
