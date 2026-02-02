package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.converter.event.base.ResponsibleManagerConverter;
import com.scnsoft.eldermark.dto.basic.PersonDto;
import com.scnsoft.eldermark.entity.event.EventManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ResponsibleManagerPersonDtoConverter extends ResponsibleManagerConverter<PersonDto> {
    @Override
    protected PersonDto create() {
        return new PersonDto();
    }

    @Override
    protected void fill(EventManager manager, PersonDto managerDto) {
        super.fill(manager, managerDto);

        managerDto.setId(managerDto.getId());
    }
}
