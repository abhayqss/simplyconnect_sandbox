package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.EncounterDto;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import com.scnsoft.eldermark.web.entity.ListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EncounterConverter implements Converter<Encounter, EncounterDto> {

    @Autowired
    private Populator<Encounter, EncounterInfoDto> encounterInfoPopulator;

    @Autowired
    private Populator<Encounter, EncounterDto> encounterPopulator;

    @Override
    public EncounterDto convert(final Encounter src) {
        if (src == null) {
            return null;
        }
        final EncounterDto dest = new EncounterDto();
        encounterInfoPopulator.populate(src, dest);
        encounterPopulator.populate(src, dest);
        return dest;
    }

}
