package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.ParticipantFacade;
import com.scnsoft.eldermark.service.ParticipantService;
import com.scnsoft.eldermark.web.entity.ParticipantDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class ParticipantFacadeImpl extends BasePhrFacade implements ParticipantFacade {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private Converter<Participant, ParticipantDto> participantConverter;

    @Override
    public ParticipantDto getParticipant(final Long participantId) {
        final Participant participant = getParticipantService().getParticipant(participantId);
//        validateAssociation(participant.getResident().getId());//TODO
        return getParticipantConverter().convert(participant);
    }

    public ParticipantService getParticipantService() {
        return participantService;
    }

    public void setParticipantService(final ParticipantService participantService) {
        this.participantService = participantService;
    }

    public Converter<Participant, ParticipantDto> getParticipantConverter() {
        return participantConverter;
    }

    public void setParticipantConverter(final Converter<Participant, ParticipantDto> participantConverter) {
        this.participantConverter = participantConverter;
    }
}
