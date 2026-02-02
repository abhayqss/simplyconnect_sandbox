package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ParticipantDao;
import com.scnsoft.eldermark.entity.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ParticipantService {

    @Autowired
    private ParticipantDao participantDao;

    public Participant getParticipant(Long participantId) {
        return getParticipantDao().getOne(participantId);
    }

    public ParticipantDao getParticipantDao() {
        return participantDao;
    }

    public void setParticipantDao(final ParticipantDao participantDao) {
        this.participantDao = participantDao;
    }
}
