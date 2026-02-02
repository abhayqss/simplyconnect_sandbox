package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.web.entity.ParticipantListItemDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class ParticipantListItemPopulator implements Populator<Participant, ParticipantListItemDto> {

    @Override
    public void populate(Participant src, ParticipantListItemDto target) {
        target.setId(src.getId());
        if (src.getRoleCode() != null) {
            target.setParticipantRole(src.getRoleCode().getDisplayName());
        }
        if (src.getPerson() != null && CollectionUtils.isNotEmpty(src.getPerson().getNames())) {
            target.setName(src.getPerson().getNames().get(0).getFullName());
        }
    }
}
