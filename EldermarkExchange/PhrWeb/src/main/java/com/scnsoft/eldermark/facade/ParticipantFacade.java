package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.web.entity.EncounterDto;
import com.scnsoft.eldermark.web.entity.ParticipantDto;

public interface ParticipantFacade {
    ParticipantDto getParticipant(Long participantId);
}
