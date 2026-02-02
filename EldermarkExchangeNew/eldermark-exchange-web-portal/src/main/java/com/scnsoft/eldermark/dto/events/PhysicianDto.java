package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.TreatingPhysicianViewData;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class PhysicianDto implements TreatingPhysicianViewData {

    private Long id;
    @Size(max = 256)
    private String firstName;
    private boolean hasAddress;
    @Valid
    private AddressDto address;
    private String phone;
    @Size(max = 256)
    private String lastName;

    private String fullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean getHasAddress() {
		return hasAddress;
	}

	public void setHasAddress(boolean hasAddress) {
		this.hasAddress = hasAddress;
	}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
