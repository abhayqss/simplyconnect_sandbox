package com.scnsoft.eldermark.dto.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.NameDto;
import com.scnsoft.eldermark.dto.event.PersonViewData;

import javax.validation.Valid;

public class PersonDto extends NameDto implements PersonViewData {
    private Long id;
    private String email;
    private String phone;
    private boolean hasAddress;

    @Valid
    private AddressDto address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(boolean hasAddress) {
        this.hasAddress = hasAddress;
    }

    @Override
    public AddressDto getAddress() {
        return address;
    }

    @Override
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getDisplayName();
    }
}
