package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Encounter;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.List;

public interface EncounterService {

    List<Encounter> findAllConsanaEncountersByResident(Resident resident);

    Encounter saveEncounter(Encounter encounter);

    void delete(Encounter encounter);

    void addProblemObservationToEncounterByConsanaId(ProblemObservation problemObservation, String encounterConsanaId);
}
