package com.scnsoft.eldermark.consana.sync.server.service.converter;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class ConsanaSyncWebToQueueDtoConverter implements Converter<ConsanaSyncDto, ReceiveConsanaPatientQueueDto> {

    private static final EnumMap<ConsanaSyncDto.UpdateTypeEnum, ConsanaPatientUpdateType> updateTypeMapping =
            new EnumMap<>(ConsanaSyncDto.UpdateTypeEnum.class);

    static {
        updateTypeMapping.put(ConsanaSyncDto.UpdateTypeEnum.PATIENT_UPDATE, ConsanaPatientUpdateType.PATIENT_UPDATE);
        updateTypeMapping.put(ConsanaSyncDto.UpdateTypeEnum.MAP_CLOSED, ConsanaPatientUpdateType.MAP_CLOSED);
    }

    @Override
    public ReceiveConsanaPatientQueueDto convert(ConsanaSyncDto consanaSyncDto) {
        return new ReceiveConsanaPatientQueueDto(
                consanaSyncDto.getIdentifier(),
                consanaSyncDto.getOrganizationId(),
                consanaSyncDto.getCommunityId(),
                updateTypeMapping.get(consanaSyncDto.getUpdateType())
        );
    }

}
