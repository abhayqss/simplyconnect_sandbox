package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterDao extends JpaRepository<Encounter, Long> {

    List<Encounter> getAllByConsanaIdIsNotNullAndResidentId(Long residentId);

    Encounter getByConsanaId(String consanaId);
}
