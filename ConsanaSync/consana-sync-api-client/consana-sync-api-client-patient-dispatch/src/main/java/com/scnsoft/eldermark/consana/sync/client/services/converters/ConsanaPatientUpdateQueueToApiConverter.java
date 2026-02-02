package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class ConsanaPatientUpdateQueueToApiConverter implements Converter<ConsanaPatientUpdateQueueDto, ConsanaSyncApiDto> {

    @Override
    public ConsanaSyncApiDto convert(@Nonnull ConsanaPatientUpdateQueueDto consanaPatientUpdateQueueDto) {
        //todo write test
        return new ConsanaSyncApiDto(
                consanaPatientUpdateQueueDto.getPatientId(),
                consanaPatientUpdateQueueDto.getOrganizationId(),
                consanaPatientUpdateQueueDto.getCommunityId(),
                consanaPatientUpdateQueueDto.getUpdateType()
        );
    }
}
