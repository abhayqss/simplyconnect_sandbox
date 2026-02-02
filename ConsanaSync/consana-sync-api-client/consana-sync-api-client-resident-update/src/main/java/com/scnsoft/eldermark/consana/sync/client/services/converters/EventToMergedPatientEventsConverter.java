package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.dao.EventDao;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.EventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.predicates.ConsanaIntegrationEnabledPredicate;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class EventToMergedPatientEventsConverter implements Converter<EventCreatedQueueDto, List<ConsanaEventCreatedQueueDto>> {

    private static final Logger logger = LoggerFactory.getLogger(EventToMergedPatientEventsConverter.class);

    private final EventDao eventDao;
    private final ResidentService residentService;
    private final ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate;
    private final SqlServerService sqlServerService;

    @Autowired
    public EventToMergedPatientEventsConverter(EventDao eventDao, ResidentService residentService, ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate, SqlServerService sqlServerService) {
        this.eventDao = eventDao;
        this.residentService = residentService;
        this.consanaIntegrationEnabledPredicate = consanaIntegrationEnabledPredicate;
        this.sqlServerService = sqlServerService;
    }

    @Override
    @NonNull
    public List<ConsanaEventCreatedQueueDto> convert(@NonNull EventCreatedQueueDto eventCreatedDto) {
        sqlServerService.openKey();
        var residentsStream = eventDao.findById(eventCreatedDto.getEventId()).stream()
                .filter(e -> !e.getEventType().isService())
                .flatMap(event -> residentService.getMergedResidents(event.getResident().getId()));
        return residentsStream
                .filter(consanaIntegrationEnabledPredicate)
                .map(residentService::updateXrefId)
                .map(resident -> buildConsanaEventCreatedQueueDto(eventCreatedDto.getEventId(), resident))
                .collect(Collectors.toList());
    }

    private ConsanaEventCreatedQueueDto buildConsanaEventCreatedQueueDto(Long eventId, Resident resident) {
        logger.debug("Building ConsanaEventCreatedQueueDto for eventId {} and {}", eventId, resident);
        return new ConsanaEventCreatedQueueDto(eventId, resident.getId());
    }
}
