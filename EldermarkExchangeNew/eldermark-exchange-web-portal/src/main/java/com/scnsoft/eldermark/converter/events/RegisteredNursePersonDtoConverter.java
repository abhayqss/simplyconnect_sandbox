package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.RegisteredNurseConverter;
import com.scnsoft.eldermark.dto.basic.PersonDto;
import com.scnsoft.eldermark.entity.event.EventRN;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class RegisteredNursePersonDtoConverter extends RegisteredNurseConverter<PersonDto> {
    @Override
    protected PersonDto create() {
        return new PersonDto();
    }

    @Override
    protected void fill(EventRN rn, PersonDto nurseDto) {
        super.fill(rn, nurseDto);

        nurseDto.setId(rn.getId());
        if (rn.getEventAddress() != null) {
            nurseDto.setHasAddress(true);
        }
    }
}
