package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.TreatingHospitalViewData;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class HospitalDto implements TreatingHospitalViewData {

    private Long id;
    @Size(max = 256)
    private String name;
    @Valid
    private AddressDto address;
    private String phone;
    private boolean hasAddress;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

	public boolean getHasAddress() {
		return hasAddress;
	}

	public void setHasAddress(boolean hasAddress) {
		this.hasAddress = hasAddress;
	}
}
