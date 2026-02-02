package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.ProblemObservationDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(noRollbackFor = Exception.class)
public class ProblemObservationServiceImpl implements ProblemObservationService {

    @Autowired
    private ProblemObservationDao problemObservationDao;

    @Override
    public List<ProblemObservation> findAllConsanaProblemObservationsByResident(Resident resident) {
        return problemObservationDao.getAllByConsanaIdIsNotNullAndProblem_ResidentId(resident.getId());
    }

    @Override
    public ProblemObservation saveProblemObservation(ProblemObservation problemObservation) {
        return problemObservationDao.save(problemObservation);
    }

    @Override
    public void delete(ProblemObservation problemObservation) {
        problemObservationDao.delete(problemObservation);
    }
}