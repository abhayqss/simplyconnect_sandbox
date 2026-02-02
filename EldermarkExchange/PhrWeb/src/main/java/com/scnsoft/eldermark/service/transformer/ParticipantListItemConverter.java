package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.ParticipantDto;
import com.scnsoft.eldermark.web.entity.ParticipantListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ParticipantListItemConverter implements Converter<Participant, ParticipantListItemDto> {

    @Autowired
    private Populator<Participant, ParticipantListItemDto> participantListItemPopulator;

    @Override
    public ParticipantListItemDto convert(Participant participant) {
        final ParticipantListItemDto result = new ParticipantListItemDto();
        getParticipantListItemPopulator().populate(participant, result);
        return result;
    }

    public Populator<Participant, ParticipantListItemDto> getParticipantListItemPopulator() {
        return participantListItemPopulator;
    }
}
