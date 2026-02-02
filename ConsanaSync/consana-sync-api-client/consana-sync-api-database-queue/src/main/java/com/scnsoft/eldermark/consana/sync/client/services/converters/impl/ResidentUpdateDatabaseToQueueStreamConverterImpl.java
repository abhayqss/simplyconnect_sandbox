package com.scnsoft.eldermark.consana.sync.client.services.converters.impl;

import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import com.scnsoft.eldermark.consana.sync.client.services.converters.ResidentUpdateDatabaseToQueueStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ResidentUpdateDatabaseToQueueStreamConverterImpl implements ResidentUpdateDatabaseToQueueStreamConverter {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateDatabaseToQueueStreamConverterImpl.class);

    //combines update types for the same resident into set and takes latest update date
    @Override
    public Stream<ResidentUpdateQueueDto> apply(Stream<ResidentUpdateDatabaseQueueBody> residentUpdateDatabaseQueueBodyStream) {
        var tmp = residentUpdateDatabaseQueueBodyStream
                .collect(Collector.of(
                        HashMap::new,
                        (map, body) -> map.put(body.getResidentId(),
                                merge(map.getOrDefault(body.getResidentId(), buildDtoFromQueueBody(body)), body)),
                        this::combineMaps,
                        Map::values
                ));
        return tmp.stream();
    }

    private ResidentUpdateQueueDto buildDtoFromQueueBody(ResidentUpdateDatabaseQueueBody queueBody) {
        return new ResidentUpdateQueueDto(
                queueBody.getResidentId(),
                EnumSet.of(queueBody.getUpdateType()),
                queueBody.getUpdateTime());
    }

    private ResidentUpdateQueueDto merge(ResidentUpdateQueueDto residentUpdateDto, ResidentUpdateDatabaseQueueBody queueBody) {
        return merge(residentUpdateDto, EnumSet.of(queueBody.getUpdateType()), queueBody.getUpdateTime());
    }

    private ResidentUpdateQueueDto merge(ResidentUpdateQueueDto residentUpdateDto1, ResidentUpdateQueueDto residentUpdateDto2) {
        return merge(residentUpdateDto1, residentUpdateDto2.getUpdateTypes(), residentUpdateDto2.getUpdateTime());
    }

    private ResidentUpdateQueueDto merge(ResidentUpdateQueueDto residentUpdateDto, Set<ResidentUpdateType> updateTypes, Long updateTime) {
        residentUpdateDto.getUpdateTypes().addAll(updateTypes);
        if (updateTime > residentUpdateDto.getUpdateTime()) {
            residentUpdateDto.setUpdateTime(updateTime);
        }
        return residentUpdateDto;
    }


    private HashMap<Long, ResidentUpdateQueueDto> combineMaps(Map<Long, ResidentUpdateQueueDto> map1, Map<Long, ResidentUpdateQueueDto> map2) {
        return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, this::merge, HashMap::new));
    }
}
