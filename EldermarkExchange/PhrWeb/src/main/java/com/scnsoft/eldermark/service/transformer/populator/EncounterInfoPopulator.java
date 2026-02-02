package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.web.entity.EncounterInfoDto;
import org.springframework.stereotype.Component;

@Component
public class EncounterInfoPopulator implements Populator<Encounter, EncounterInfoDto> {
    @Override
    public void populate(final Encounter src, final EncounterInfoDto target) {
        if (src == null) {
            return;
        }
        target.setId(src.getId());
        target.setEncounterName(src.getEncounterTypeText());
        if (src.getEffectiveTime() != null) {
            target.setStartDateTime(src.getEffectiveTime().getTime());
        }
    }
}
