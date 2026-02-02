package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.web.entity.TelecomsDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PersonTelecomConverter implements Converter<PersonTelecom, TelecomsDto> {
    @Override
    public TelecomsDto convert(final PersonTelecom personTelecom) {
        if (personTelecom == null) {
            return null;
        }
        final TelecomsDto dto = new TelecomsDto();
//        dto.setPhone(personTelecom.get);
        return dto;
    }
}
