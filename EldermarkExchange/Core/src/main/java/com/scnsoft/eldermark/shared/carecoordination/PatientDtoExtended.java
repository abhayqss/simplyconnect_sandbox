package com.scnsoft.eldermark.shared.carecoordination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by averazub on 12/5/2016.
 */
public class PatientDtoExtended extends PatientDto {
    List<AddressDto> addressList;
    String legacyId;

    public PatientDtoExtended() {
    }

    public PatientDtoExtended(PatientDto other) {
        super(other);
        this.addressList = new ArrayList<AddressDto>();
        this.addressList.add(address);
    }

    public List<AddressDto> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<AddressDto> addressList) {
        this.addressList = addressList;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }
}
