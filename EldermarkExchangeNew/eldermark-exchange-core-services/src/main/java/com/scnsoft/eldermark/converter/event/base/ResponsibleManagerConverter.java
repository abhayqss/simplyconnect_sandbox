package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.dto.event.PersonViewData;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class ResponsibleManagerConverter<P extends PersonViewData> implements Converter<Event, P> {

    @Override
    public P convert(Event event) {
        var manager = event.getEventManager();
        if (manager == null) {
            return null;
        }

        var managerDto = create();
        fill(manager, managerDto);
        return managerDto;
    }

    protected void fill(EventManager manager, P managerDto) {
        managerDto.setFirstName(manager.getFirstName());
        managerDto.setLastName(manager.getLastName());
        managerDto.setPhone(StringUtils.trimToNull(manager.getPhone()));
        managerDto.setEmail(manager.getEmail());
    }

    protected abstract P create();

}
