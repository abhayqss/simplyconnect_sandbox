package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.PersonViewData;
import com.scnsoft.eldermark.util.CareCoordinationUtils;

public class PersonNotificationDto implements PersonViewData {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private AddressDto address;


    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
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
    public AddressDto getAddress() {
        return address;
    }

    @Override
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getName() {
        return CareCoordinationUtils.getFullName(firstName, lastName);
    }
}
