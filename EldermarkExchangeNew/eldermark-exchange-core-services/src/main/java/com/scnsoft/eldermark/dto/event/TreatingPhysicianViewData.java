package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.AddressDto;

public interface TreatingPhysicianViewData {
    String getFullName();

    void setFullName(String name);

    AddressDto getAddress();

    void setAddress(AddressDto address);

    String getPhone();

    void setPhone(String phone);
}
