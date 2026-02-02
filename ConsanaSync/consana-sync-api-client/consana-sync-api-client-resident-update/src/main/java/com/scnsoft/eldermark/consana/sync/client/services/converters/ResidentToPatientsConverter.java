package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import com.scnsoft.eldermark.consana.sync.client.predicates.ConsanaIntegrationEnabledPredicate;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Transactional
public class ResidentToPatientsConverter implements Converter<ResidentUpdateQueueDto, List<ConsanaPatientUpdateQueueDto>> {

    private static final Logger logger = LoggerFactory.getLogger(ResidentToPatientsConverter.class);

    private final ResidentService residentService;
    private final ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate;
    private final SqlServerService sqlServerService;

    private static final Set<ResidentUpdateType> MERGED_NOT_AFFECTED_RESIDENT_TYPES =
            EnumSet.of(ResidentUpdateType.RESIDENT, ResidentUpdateType.RESIDENT_MERGE);

    @Autowired
    public ResidentToPatientsConverter(ResidentService residentService, ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate, SqlServerService sqlServerService) {
        this.residentService = residentService;
        this.consanaIntegrationEnabledPredicate = consanaIntegrationEnabledPredicate;
        this.sqlServerService = sqlServerService;
    }

    @Override
    @NonNull
    public List<ConsanaPatientUpdateQueueDto> convert(@NonNull ResidentUpdateQueueDto residentUpdateDto) {
        sqlServerService.openKey();
        final Stream<Resident> residentsStream;
        if (isAffectsMerged(residentUpdateDto)) {
            residentsStream = residentService.getMergedResidents(residentUpdateDto.getResidentId());
        } else {
            residentsStream = Stream.of(residentService.getOne(residentUpdateDto.getResidentId()));
        }
        return residentsStream
                .filter(consanaIntegrationEnabledPredicate)
                .map(residentService::updateXrefId)
                .map(resident -> buildConsanaPatientUpdateDto(resident, residentUpdateDto.getUpdateTime()))
                .collect(Collectors.toList());
    }

    private boolean isAffectsMerged(@NonNull ResidentUpdateQueueDto residentUpdateDto) {
        return CollectionUtils.emptyIfNull(residentUpdateDto.getUpdateTypes())
                .stream()
                .anyMatch(residentUpdateType -> !MERGED_NOT_AFFECTED_RESIDENT_TYPES.contains(residentUpdateType));
    }

    private ConsanaPatientUpdateQueueDto buildConsanaPatientUpdateDto(Resident resident, Long updateTime) {
        logger.debug("Building ConsanaPatientUpdateQueueDto for {}", resident);
        return new ConsanaPatientUpdateQueueDto(
                resident.getConsanaXrefId(),
                resident.getDatabase().getConsanaXOwningId(), resident.getFacility().getConsanaOrgId(),
                ConsanaPatientUpdateType.PATIENT_UPDATE, updateTime);
    }
}
