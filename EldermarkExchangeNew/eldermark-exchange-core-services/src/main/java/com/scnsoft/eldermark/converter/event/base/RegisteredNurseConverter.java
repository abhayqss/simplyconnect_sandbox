package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.PersonViewData;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAddress;
import com.scnsoft.eldermark.entity.event.EventRN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class RegisteredNurseConverter<P extends PersonViewData> implements Converter<Event, P> {

    @Autowired
    private Converter<EventAddress, AddressDto> addressDtoConverter;

    @Override
    public P convert(Event event) {
        var rn = event.getEventRn();
        if (rn == null) {
            return null;
        }
        var nurseDto = create();
        fill(rn, nurseDto);
        return nurseDto;
    }

    protected void fill(EventRN rn, P nurseDto) {
        nurseDto.setFirstName(rn.getFirstName());
        nurseDto.setLastName(rn.getLastName());
        nurseDto.setAddress(addressDtoConverter.convert(rn.getEventAddress()));
    }

    protected abstract P create();
}
