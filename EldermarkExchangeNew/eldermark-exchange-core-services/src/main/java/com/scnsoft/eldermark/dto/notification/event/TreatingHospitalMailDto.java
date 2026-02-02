package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.TreatingHospitalViewData;

public class TreatingHospitalMailDto implements TreatingHospitalViewData {

    private String name;
    private AddressDto address;
    private String phone;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
