package com.scnsoft.eldermark.consana.sync.client.services.converters.impl;

import com.scnsoft.eldermark.consana.sync.client.TestUtils;
import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResidentUpdateDatabaseToQueueStreamConverterImplTest {

    @InjectMocks
    private ResidentUpdateDatabaseToQueueStreamConverterImpl instance;

    @Test
    void apply_WhenResidentsAreDifferent_ShouldReturnThemAll() {
        var updateTime = Instant.now().toEpochMilli();
        final List<ResidentUpdateDatabaseQueueBody> inputList = List.of(
                TestUtils.buildResidentUpdateDatabaseQueueBody(1L, ResidentUpdateType.MEDICATION, updateTime),
                TestUtils.buildResidentUpdateDatabaseQueueBody(2L, ResidentUpdateType.ALLERGY, updateTime)
        );
        final List expectedList = List.of(
                new ResidentUpdateQueueDto(1L, EnumSet.of(ResidentUpdateType.MEDICATION), updateTime),
                new ResidentUpdateQueueDto(2L, EnumSet.of(ResidentUpdateType.ALLERGY), updateTime)
        );


        var resultStream = instance.apply(inputList.stream());

        assertIterableEquals(expectedList, resultStream.collect(Collectors.toList()));
    }

    @Test
    void apply_WhenSomeResidentsAreSame_ShouldAccumulateUpdateTypesAndChooseMaxUpdateDate() {
        var updateDate = Instant.now().toEpochMilli();
        var biggerUpdateDate = updateDate + 10000;
        final List<ResidentUpdateDatabaseQueueBody> inputList = List.of(
                TestUtils.buildResidentUpdateDatabaseQueueBody(1L, ResidentUpdateType.MEDICATION, updateDate),
                TestUtils.buildResidentUpdateDatabaseQueueBody(1L, ResidentUpdateType.ALLERGY, biggerUpdateDate),
                TestUtils.buildResidentUpdateDatabaseQueueBody(2L, ResidentUpdateType.CARE_TEAM, updateDate)
        );
        final List expectedList = List.of(
                new ResidentUpdateQueueDto(1L, EnumSet.of(ResidentUpdateType.MEDICATION, ResidentUpdateType.ALLERGY), biggerUpdateDate),
                new ResidentUpdateQueueDto(2L, EnumSet.of(ResidentUpdateType.CARE_TEAM), updateDate)
        );

        var resultStream = instance.apply(inputList.stream());

        assertIterableEquals(expectedList, resultStream.collect(Collectors.toList()));
    }
}