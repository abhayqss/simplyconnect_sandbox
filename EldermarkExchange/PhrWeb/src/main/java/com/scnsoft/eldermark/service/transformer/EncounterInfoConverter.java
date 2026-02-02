package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EncounterInfoConverter implements Converter<Encounter, EncounterInfoDto> {

    @Autowired
    private Populator<Encounter, EncounterInfoDto> encounterInfoPopulator;

    @Override
    public EncounterInfoDto convert(final Encounter src) {
        if (src == null) {
            return null;
        }
        final EncounterInfoDto dest = new EncounterInfoDto();
        getEncounterInfoPopulator().populate(src, dest);
        return dest;
    }

    public Populator<Encounter, EncounterInfoDto> getEncounterInfoPopulator() {
        return encounterInfoPopulator;
    }

    public void setEncounterInfoPopulator(final Populator<Encounter, EncounterInfoDto> encounterInfoPopulator) {
        this.encounterInfoPopulator = encounterInfoPopulator;
    }
}
