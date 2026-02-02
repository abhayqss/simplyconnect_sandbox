package com.scnsoft.eldermark.consana.sync.client.services.consumers.impl;

import com.scnsoft.eldermark.consana.sync.client.consana.ConsanaGateway;
import com.scnsoft.eldermark.consana.sync.client.exceptions.ConsanaDispatchException;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEventCreatedApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Event;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.client.services.EventService;
import com.scnsoft.eldermark.consana.sync.client.services.ResidentService;
import com.scnsoft.eldermark.consana.sync.client.services.consumers.EventDispatchQueueConsumer;
import com.scnsoft.eldermark.consana.sync.client.services.consumers.PatientDispatchQueueConsumer;
import com.scnsoft.eldermark.consana.sync.client.services.logging.DispatchLogService;
import com.scnsoft.eldermark.consana.sync.client.services.producer.EventDispatchQueueProducer;
import com.scnsoft.eldermark.consana.sync.client.services.senders.ConsanaSyncApiSender;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.hl7.fhir.instance.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventDispatchQueueConsumerImpl implements EventDispatchQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventDispatchQueueConsumerImpl.class);

    private final Converter<Pair<Event, Pair<Resident, Patient>>, ConsanaEventCreatedApiDto> eventConverter;
    private final ConsanaSyncApiSender consanaSyncApiSender;
    private final SqlServerService sqlServerService;
    private final DispatchLogService dispatchLogService;
    private final EventService eventService;
    private final ResidentService residentService;
    private final EventDispatchQueueProducer eventDispatchQueueProducer;
    private final PatientDispatchQueueConsumer patientDispatchQueueConsumer;
    private final ConsanaGateway consanaGateway;
    private final Map<Long, Integer> syncAttemptCounts;
    private final Map<Long, Instant> latestSyncAttemptsTime;

    @Value("${consana.sync.event.pause.between.attempts}")
    private long pauseBetweenAttempts;

    @Value("${consana.sync.event.max.attempts}")
    private int maxSyncAttempts;

    @Value("${consana.sync.event.retry.sync.patient.attempts}")
    private int retrySyncPatientAttempts;

    @Value("${consana.events.push.enabled}")
    private boolean eventsPushEnabled;

    @Autowired
    public EventDispatchQueueConsumerImpl(Converter<Pair<Event, Pair<Resident, Patient>>, ConsanaEventCreatedApiDto> eventConverter, ConsanaSyncApiSender consanaSyncApiSender, SqlServerService sqlServerService, DispatchLogService dispatchLogService, EventService eventService, ResidentService residentService, EventDispatchQueueProducer eventDispatchQueueProducer, PatientDispatchQueueConsumer patientDispatchQueueConsumer, ConsanaGateway consanaGateway) {
        this.eventConverter = eventConverter;
        this.consanaSyncApiSender = consanaSyncApiSender;
        this.sqlServerService = sqlServerService;
        this.dispatchLogService = dispatchLogService;
        this.eventService = eventService;
        this.residentService = residentService;
        this.eventDispatchQueueProducer = eventDispatchQueueProducer;
        this.patientDispatchQueueConsumer = patientDispatchQueueConsumer;
        this.consanaGateway = consanaGateway;
        this.syncAttemptCounts = new ConcurrentHashMap<>();
        this.latestSyncAttemptsTime = new ConcurrentHashMap<>();
    }

    @JmsListener(
            destination = "${queue.consanaEventDispatch.destination}",
            concurrency = "${queue.consanaEventDispatch.concurrency}",
            containerFactory = "eventDispatchJmsListenerContainerFactory"
    )
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void consume(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        logger.info("Received " + consanaEventCreatedQueueDto);
        if (!eventsPushEnabled) {
            logger.info("Events pushing disabled");
            return;
        }
        Resident resident = null;
        try {
            sqlServerService.openKey();
            var event = eventService.findById(consanaEventCreatedQueueDto.getEventId())
                    .orElseThrow(() -> new ConsanaDispatchException("Event wasn't found by id " + consanaEventCreatedQueueDto.getEventId()));
            resident = residentService.findById(consanaEventCreatedQueueDto.getResidentId())
                    .orElseThrow(() -> new ConsanaDispatchException("Resident wasn't found by id " + consanaEventCreatedQueueDto.getResidentId()));
            var patient = consanaGateway.getPatient(resident.getConsanaXrefId(), resident.getDatabase().getConsanaXOwningId());
            if (patient == null) {
                waitingPatientSync(consanaEventCreatedQueueDto, resident);
                return;
            }

            cleanUpCaches(consanaEventCreatedQueueDto);
            var consanaEvent = eventConverter.convert(Pair.of(event, Pair.of(resident, patient)));
            consanaSyncApiSender.sendEvent(consanaEvent);

            logger.info("{} processed", consanaEventCreatedQueueDto);
            dispatchLogService.logSuccess(consanaEventCreatedQueueDto, resident);
        } catch (Exception e) {
            logger.warn("Error during processing {}", consanaEventCreatedQueueDto, e);
            dispatchLogService.logFail(consanaEventCreatedQueueDto, resident, e);
        }
    }

    private void waitingPatientSync(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto, Resident resident) {
        if (!isWaitingTimeExpired(consanaEventCreatedQueueDto)) {
            eventDispatchQueueProducer.send(consanaEventCreatedQueueDto);
            return;
        }

        var syncAttempts = getCachedSyncAttempts(consanaEventCreatedQueueDto);
        if (syncAttempts >= maxSyncAttempts) {
            throw new ConsanaDispatchException("Events wasn't synced " + consanaEventCreatedQueueDto + " after attempts: " + maxSyncAttempts);
        }

        var now = Instant.now();
        if (syncAttempts == retrySyncPatientAttempts) {
            var consanaPatientUpdateDto =
                    new ConsanaPatientUpdateQueueDto(resident.getConsanaXrefId(), resident.getDatabase().getConsanaXOwningId(),
                            resident.getFacility().getConsanaOrgId(), ConsanaPatientUpdateType.PATIENT_UPDATE, now.getEpochSecond());
            var infoMessage = String
                    .format("Due to patient was not synced after %s sync event attempts, send patient %s for syncing for dto %s", retrySyncPatientAttempts, consanaEventCreatedQueueDto, consanaPatientUpdateDto);
            logger.info(infoMessage);
            dispatchLogService.logInfo(consanaEventCreatedQueueDto, resident, infoMessage);
            patientDispatchQueueConsumer.consume(consanaPatientUpdateDto);
        }
        cacheSyncAttempts(consanaEventCreatedQueueDto, ++syncAttempts);
        cacheAttemptTime(consanaEventCreatedQueueDto, now);
        eventDispatchQueueProducer.send(consanaEventCreatedQueueDto);
        logger.info("Attempt {} to sync event {}, but patient is empty yet", syncAttempts, consanaEventCreatedQueueDto);
        dispatchLogService.logInfo(consanaEventCreatedQueueDto, resident, "Attempt " + syncAttempts + " to sync event, but patient is empty yet");
    }

    private boolean isWaitingTimeExpired(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        var cachedUpdateDate = getCachedLatestAttemptTime(consanaEventCreatedQueueDto);
        return cachedUpdateDate == null || cachedUpdateDate.toEpochMilli() + pauseBetweenAttempts < Instant.now().toEpochMilli();
    }

    private Instant getCachedLatestAttemptTime(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        return latestSyncAttemptsTime.getOrDefault(consanaEventCreatedQueueDto.getEventId(), null);
    }

    private void cacheAttemptTime(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto, Instant when) {
        latestSyncAttemptsTime.put(consanaEventCreatedQueueDto.getEventId(), when);
    }

    private int getCachedSyncAttempts(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        return syncAttemptCounts.getOrDefault(consanaEventCreatedQueueDto.getEventId(), 0);
    }

    private void cacheSyncAttempts(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto, int attempts) {
        syncAttemptCounts.put(consanaEventCreatedQueueDto.getEventId(), attempts);
    }

    private void cleanUpCaches(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto) {
        var eventId = consanaEventCreatedQueueDto.getEventId();
        syncAttemptCounts.remove(eventId);
        latestSyncAttemptsTime.remove(eventId);
    }
}
