package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.AddressDto;

public interface TreatingHospitalViewData {

    String getName();

    void setName(String name);

    AddressDto getAddress();

    void setAddress(AddressDto address);

    String getPhone();

    void setPhone(String phone);
}
