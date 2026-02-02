package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.AddressDto;

public interface PersonViewData {

    String getEmail();

    void setEmail(String email);

    String getPhone();

    void setPhone(String phone);

    AddressDto getAddress();

    void setAddress(AddressDto address);

    String getName();

    void setFirstName(String name);

    void setLastName(String name);
}
