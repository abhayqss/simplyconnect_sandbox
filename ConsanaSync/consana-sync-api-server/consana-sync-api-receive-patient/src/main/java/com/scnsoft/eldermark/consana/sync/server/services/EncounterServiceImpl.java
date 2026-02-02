package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.EncounterDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Encounter;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(noRollbackFor = Exception.class)
public class EncounterServiceImpl implements EncounterService {

    @Autowired
    private EncounterDao encounterDao;

    @Override
    public List<Encounter> findAllConsanaEncountersByResident(Resident resident) {
        return encounterDao.getAllByConsanaIdIsNotNullAndResidentId(resident.getId());
    }

    @Override
    public Encounter saveEncounter(Encounter encounter) {
        return encounterDao.save(encounter);
    }

    @Override
    public void delete(Encounter encounter) {
        encounterDao.delete(encounter);
    }

    @Override
    public void addProblemObservationToEncounterByConsanaId(ProblemObservation problemObservation, String encounterConsanaId) {
        var encounter = encounterDao.getByConsanaId(encounterConsanaId);
        if (encounter != null) {
            encounter.setProblemObservation(problemObservation);
            encounterDao.save(encounter);
        }
    }
}
