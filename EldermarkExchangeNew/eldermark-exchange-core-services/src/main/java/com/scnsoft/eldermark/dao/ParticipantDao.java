package com.scnsoft.eldermark.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.ccd.Participant;

@Repository
public interface ParticipantDao extends JpaRepository<Participant, Long> {

    @Query("select l from Participant l where l.client.id IN (:residentIds) and l.legacyTable = 'Contact' order by l.priority")
    List<Participant> listByClientIds(@Param("residentIds") List<Long> clientIds);

    @Query("select l from Participant l where l.client.id IN (:residentIds) and l.legacyTable = 'Contact' order by l.priority")
    Set<Participant> listCcdParticipants(@Param("residentIds") Collection<Long> clientIds);

    @Query("select l from Participant l where l.client.id IN (:residentIds) and is_responsible_party is true")
    Set<Participant> listResponsibleParties(@Param("residentIds") List<Long> residentIds);

    void deleteAllByClientId(Long clientId);
}
