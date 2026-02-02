package com.scnsoft.eldermark.converter.event;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class XADPatientAddressConverter implements ListAndItemConverter<XADPatientAddress, AddressDto> {

    @Autowired
    private StateService StateService;

    @Override
    public AddressDto convert(XADPatientAddress source) {
        AddressDto target = null;
        if (source != null) {
            target = new AddressDto();
            target.setStreet(source.getStreetAddress());
            target.setCity(source.getCity());
            target.setZip(source.getZip());
            State state = StateService.findByAbbr(source.getState());
            if (state != null) {
                target.setStateId(state.getId());
                target.setStateName(state.getName());
            }
        }
        return target;
    }

}
