package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.google.common.cache.Cache;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.services.consumers.PatientDispatchQueueConsumer;
import com.scnsoft.eldermark.consana.sync.client.services.logging.DispatchLogService;
import com.scnsoft.eldermark.consana.sync.client.services.senders.ConsanaSyncApiSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PatientDispatchQueueConsumerImpl implements PatientDispatchQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PatientDispatchQueueConsumerImpl.class);

    private final Cache<String, Instant> processedPatientsCache;
    private final Converter<ConsanaPatientUpdateQueueDto, ConsanaSyncApiDto> converter;
    private final ConsanaSyncApiSender consanaSyncApiSender;
    private final DispatchLogService dispatchLogService;

    @Autowired
    public PatientDispatchQueueConsumerImpl(Cache<String, Instant> processedPatientsCache, Converter<ConsanaPatientUpdateQueueDto,
            ConsanaSyncApiDto> converter, ConsanaSyncApiSender consanaSyncApiSender, DispatchLogService dispatchLogService) {
        this.processedPatientsCache = processedPatientsCache;
        this.converter = converter;
        this.consanaSyncApiSender = consanaSyncApiSender;
        this.dispatchLogService = dispatchLogService;
    }

    @JmsListener(
            destination = "${queue.dispatchConsanaPatient.destination}",
            concurrency = "${queue.dispatchConsanaPatient.concurrency}",
            containerFactory = "patientDispatchJmsListenerContainerFactory"
    )
    @Override
    public void consume(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        logger.info("Received {}", consanaPatientUpdateDto);
        try {

            //update types check isn't needed because there is only one update type: PATIENT_UPDATE
            if (cachedUpdateDateBeforeCurrentUpdate(consanaPatientUpdateDto)) {
                var now = Instant.now();
                sendSyncNotification(consanaPatientUpdateDto);
                cacheProcessedDateForPatient(consanaPatientUpdateDto, now);
                dispatchLogService.logSuccess(consanaPatientUpdateDto);
            } else {
                logAlreadyProcessedSync(consanaPatientUpdateDto);
            }
        } catch (Exception e) {
            logger.warn("Error during processing {}", consanaPatientUpdateDto, e);
            dispatchLogService.logFail(consanaPatientUpdateDto, e);
        }
    }

    private boolean cachedUpdateDateBeforeCurrentUpdate(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        var cachedUpdateDate = getCachedUpdateTime(consanaPatientUpdateDto);
        return cachedUpdateDate == null || cachedUpdateDate.toEpochMilli() < consanaPatientUpdateDto.getUpdateTime();
    }

    private Instant getCachedUpdateTime(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        return processedPatientsCache.getIfPresent(consanaPatientUpdateDto.getPatientId());
    }

    private void sendSyncNotification(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        var converted = converter.convert(consanaPatientUpdateDto);
        consanaSyncApiSender.sendSyncNotification(converted);
    }

    private void cacheProcessedDateForPatient(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto, Instant when) {
        processedPatientsCache.put(consanaPatientUpdateDto.getPatientId(), when);
    }

    private void logAlreadyProcessedSync(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto) {
        var cachedUpdateDate = getCachedUpdateTime(consanaPatientUpdateDto);

        logger.debug("{} was already processed at {}", consanaPatientUpdateDto,
                processedPatientsCache.getIfPresent(consanaPatientUpdateDto.getPatientId()));

        dispatchLogService.logSuccess(consanaPatientUpdateDto, cachedUpdateDate);
    }
}
