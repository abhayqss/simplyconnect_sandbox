package com.scnsoft.eldermark.api.shared.dto;

/**
 * Created by pzhurba on 12-Oct-15.
 */
public interface WithAddressDto {
    AddressDto getAddress();

    void setAddress(AddressDto address);

    boolean isIncludeAddress();

    void setIncludeAddress(boolean includeAddress);

}
