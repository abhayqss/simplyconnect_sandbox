package com.scnsoft.eldermark.mapper.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.convention.MatchingStrategies;


public class NotifyResidentMapper extends GenericMapper<NotifyResident, NotifyResidentDto> {

    {

        AbstractConverter<String, Long> toLong = new AbstractConverter<String, Long>() {
            @Override
            protected Long convert(String source) {
                return source != null ? Long.parseLong(source) : null;
            }

        };

        AbstractConverter<Long, String> toString = new AbstractConverter<Long, String>() {
            @Override
            protected String convert(Long source) {
                return source != null ? source.toString() : null;
            }

        };

        getModelMapper().createTypeMap(String.class, Long.class);
        getModelMapper().addConverter(toLong);
        getModelMapper().createTypeMap(Long.class, String.class);
        getModelMapper().addConverter(toString);
        getModelMapper().getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    }


    @Override
    protected Class<NotifyResident> getEntityClass() {
        return NotifyResident.class;
    }

    @Override
    protected Class<NotifyResidentDto> getDtoClass() {
        return NotifyResidentDto.class;
    }
}
