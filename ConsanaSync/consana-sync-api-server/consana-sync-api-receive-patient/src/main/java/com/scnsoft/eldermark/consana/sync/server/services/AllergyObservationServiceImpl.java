package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.AllergyObservationDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.AllergyObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(noRollbackFor = Exception.class)
public class AllergyObservationServiceImpl implements AllergyObservationService {

    @Autowired
    private AllergyObservationDao allergyObservationDao;

    @Override
    public List<AllergyObservation> findAllConsanaAllergyObservations(Resident resident) {
        return allergyObservationDao.getAllByConsanaIdIsNotNullAndAllergy_ResidentId(resident.getId());
    }

    @Override
    public AllergyObservation saveAllergyObservation(AllergyObservation allergyObservation) {
        return allergyObservationDao.save(allergyObservation);
    }

    @Override
    public void delete(AllergyObservation allergyObservation) {
        allergyObservationDao.delete(allergyObservation);
    }
}
