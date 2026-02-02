package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Participant;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ParticipantDaoImpl extends ResidentAwareDaoImpl<Participant> implements ParticipantDao {

    public ParticipantDaoImpl() {
        super(Participant.class);
    }

    @Override
    public List<Participant> listCcdParticipants(Long residentId) {
        TypedQuery<Participant> query = entityManager.createNamedQuery("participant.ccdParticipants",
                Participant.class);
        query.setParameter("residentId", residentId);
        query.setParameter("legacyTable", "Contact");
        return query.getResultList();
    }

    @Override
    public List<Participant> listResponsibleParties(Long residentId) {
        TypedQuery<Participant> query = entityManager.createNamedQuery("participant.responsibleParties",
                Participant.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public Set<Participant> listCcdParticipants(Collection<Long> residentIds) {
        TypedQuery<Participant> query = entityManager.createNamedQuery("participant.listCcdParticipants",
                Participant.class);
        query.setParameter("residentIds", residentIds);
        query.setParameter("legacyTable", "Contact");
        return new HashSet<Participant>(query.getResultList());
    }

    @Override
    public Set<Participant> listResponsibleParties(Collection<Long> residentIds) {
        TypedQuery<Participant> query = entityManager.createNamedQuery("participant.listResponsibleParties",
                Participant.class);
        query.setParameter("residentIds", residentIds);
        return new HashSet<Participant>(query.getResultList());
    }

}
