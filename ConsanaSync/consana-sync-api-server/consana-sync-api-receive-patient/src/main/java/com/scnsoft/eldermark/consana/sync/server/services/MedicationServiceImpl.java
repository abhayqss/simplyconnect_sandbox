package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.MedicationDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Medication;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(noRollbackFor = Exception.class)
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationDao medicationDao;

    @Override
    public Medication saveMedication(Medication medication) {
        return medicationDao.save(medication);
    }

    @Override
    public List<Medication> findAllConsanaMedicationsByResident(Resident resident) {
        return medicationDao.getAllByConsanaIdIsNotNullAndResidentId(resident.getId());
    }

    @Override
    public void delete(Medication medication) {
        medicationDao.delete(medication);
    }
}
