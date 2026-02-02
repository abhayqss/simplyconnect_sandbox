package com.scnsoft.eldermark.consana.sync.client.services.logging.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.ConsanaEventDispatchLogDao;
import com.scnsoft.eldermark.consana.sync.client.dao.ConsanaPatientDispatchLogDao;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.entities.logging.ConsanaDispatchLog;
import com.scnsoft.eldermark.consana.sync.client.model.entities.logging.ConsanaEventDispatchLog;
import com.scnsoft.eldermark.consana.sync.client.model.entities.logging.ConsanaPatientDispatchLog;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.logging.DispatchLogService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DispatchLogServiceImpl implements DispatchLogService {

    private final ConsanaEventDispatchLogDao eventDispatchLogDao;
    private final ConsanaPatientDispatchLogDao patientDispatchLogDao;

    @Autowired
    public DispatchLogServiceImpl(ConsanaEventDispatchLogDao eventDispatchLogDao, ConsanaPatientDispatchLogDao patientDispatchLogDao) {
        this.eventDispatchLogDao = eventDispatchLogDao;
        this.patientDispatchLogDao = patientDispatchLogDao;
    }

    @Override
    public void logSuccess(ConsanaEventCreatedQueueDto dto, Resident resident) {
        var log = createEventDispatchLog(dto, resident);
        log.setSuccess(true);

        eventDispatchLogDao.save(log);
    }

    @Override
    public void logFail(ConsanaEventCreatedQueueDto dto, Resident resident, Exception ex) {
        var log = createEventDispatchLog(dto, resident);
        fillFail(log, ex);

        eventDispatchLogDao.save(log);
    }

    @Override
    public void logInfo(ConsanaEventCreatedQueueDto dto, Resident resident, String info) {
        var log = createEventDispatchLog(dto, resident);
        log.setSuccess(false);
        log.setErrorMessage(info);

        eventDispatchLogDao.save(log);
    }

    private ConsanaEventDispatchLog createEventDispatchLog(ConsanaEventCreatedQueueDto dto, Resident resident) {
        var log = new ConsanaEventDispatchLog();

        log.setProcessDatetime(Instant.now());
        log.setEventId(dto.getEventId());
        log.setResidentId(dto.getResidentId());

        if (resident != null) {
            log.setConsanaPatientId(resident.getConsanaXrefId());
            log.setOrganizationId(resident.getDatabase().getConsanaXOwningId());
            log.setCommunityId(resident.getFacility().getConsanaOrgId());
        }

        return log;
    }

    @Override
    public void logSuccess(ConsanaPatientUpdateQueueDto dto) {
        logSuccess(dto, null);
    }

    @Override
    public void logSuccess(ConsanaPatientUpdateQueueDto dto, Instant wasAlreadySyncedAt) {
        var log = createPatientDispatchLog(dto, wasAlreadySyncedAt);
        log.setSuccess(true);

        patientDispatchLogDao.save(log);
    }

    @Override
    public void logFail(ConsanaPatientUpdateQueueDto dto, Exception ex) {
        var log = createPatientDispatchLog(dto, null);
        fillFail(log, ex);

        patientDispatchLogDao.save(log);
    }

    private ConsanaPatientDispatchLog createPatientDispatchLog(ConsanaPatientUpdateQueueDto dto, Instant wasAlreadySyncedAt) {
        var log = new ConsanaPatientDispatchLog();

        log.setProcessDatetime(Instant.now());
        log.setUpdateType(dto.getUpdateType());
        log.setUpdateTime(Instant.ofEpochMilli(dto.getUpdateTime()));
        log.setConsanaPatientId(dto.getPatientId());
        log.setOrganizationId(dto.getOrganizationId());
        log.setCommunityId(dto.getCommunityId());
        log.setWasAlreadyProcessedDatetime(wasAlreadySyncedAt);

        return log;
    }

    private void fillFail(ConsanaDispatchLog log, Exception ex) {
        log.setSuccess(false);
        log.setErrorMessage(ExceptionUtils.getStackTrace(ex));
    }
}
