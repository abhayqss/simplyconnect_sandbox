package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.web.entity.ParticipantDto;
import com.scnsoft.eldermark.web.entity.ParticipantListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ParticipantConverter implements Converter<Participant, ParticipantDto> {

    @Autowired
    private Populator<Participant, ParticipantDto> participantPopulator;

    @Autowired
    private Populator<Participant, ParticipantListItemDto> participantListItemPopulator;

    @Override
    public ParticipantDto convert(Participant participant) {
        final ParticipantDto result = new ParticipantDto();
        participantListItemPopulator.populate(participant, result);
        participantPopulator.populate(participant, result);
        return result;
    }
}
