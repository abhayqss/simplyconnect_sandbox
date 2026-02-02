package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.*;
import com.scnsoft.eldermark.consana.sync.server.log.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogServiceImpl implements LogService {

    private final ConsanaPatientLogDao patientLogDao;

    private final ConsanaMedicationLogDao medicationLogDao;

    private final ConsanaProblemObservationLogDao problemLogDao;

    private final ConsanaAllergyObservationLogDao allergyLogDao;

    private final ConsanaEncounterLogDao encounterLogDao;

    private final ConsanaMedicationActionPlanLogDao medicationActionPlanLogDao;

    @Autowired
    public LogServiceImpl(ConsanaPatientLogDao patientLogDao, ConsanaMedicationLogDao medicationLogDao, ConsanaProblemObservationLogDao problemLogDao, ConsanaAllergyObservationLogDao allergyLogDao, ConsanaEncounterLogDao encounterLogDao, ConsanaMedicationActionPlanLogDao medicationActionPlanLogDao) {
        this.patientLogDao = patientLogDao;
        this.medicationLogDao = medicationLogDao;
        this.problemLogDao = problemLogDao;
        this.allergyLogDao = allergyLogDao;
        this.encounterLogDao = encounterLogDao;
        this.medicationActionPlanLogDao = medicationActionPlanLogDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveInNewTransaction(ConsanaBaseLog log) {
        if (log instanceof ConsanaPatientLog) {
            patientLogDao.saveAndFlush((ConsanaPatientLog) log);
        }
        if (log instanceof ConsanaMedicationLog) {
            medicationLogDao.saveAndFlush((ConsanaMedicationLog) log);
        }
        if (log instanceof ConsanaProblemObservationLog) {
            problemLogDao.saveAndFlush((ConsanaProblemObservationLog) log);
        }
        if (log instanceof ConsanaAllergyObservationLog) {
            allergyLogDao.saveAndFlush((ConsanaAllergyObservationLog) log);
        }
        if (log instanceof ConsanaEncounterLog) {
            encounterLogDao.saveAndFlush((ConsanaEncounterLog) log);
        }
        if (log instanceof ConsanaMedicationActionPlanLog) {
            medicationActionPlanLogDao.saveAndFlush((ConsanaMedicationActionPlanLog) log);
        }
    }
}
