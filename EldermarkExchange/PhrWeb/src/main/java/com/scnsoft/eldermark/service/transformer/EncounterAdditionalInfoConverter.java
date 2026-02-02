package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.web.entity.EncounterAdditionalInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EncounterAdditionalInfoConverter implements Converter<Encounter, EncounterAdditionalInfoDto> {

    @Autowired
    private DataSourceConverter dataSourceConverter;

    @Override
    public EncounterAdditionalInfoDto convert(final Encounter encounter) {
        if (encounter == null) {
            return null;
        }
        final EncounterAdditionalInfoDto result = new EncounterAdditionalInfoDto();

        result.setLocation(null);//TODO and other attributes

        Long residentId = null;
        if (encounter.getResident() != null) {
            residentId = encounter.getResident().getId();
        }
        result.setDataSource(dataSourceConverter.convert(encounter.getDatabase(), residentId));
        return result;
    }
}
