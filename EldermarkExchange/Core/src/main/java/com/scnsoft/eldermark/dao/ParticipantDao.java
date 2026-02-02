package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Participant;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ParticipantDao extends ResidentAwareDao<Participant> {
    List<Participant> listCcdParticipants(Long residentId);
    List<Participant> listResponsibleParties(Long residentId);
    Set<Participant> listCcdParticipants(Collection<Long> residentIds);
    Set<Participant> listResponsibleParties(Collection<Long> residentIds);
}
