package com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;

import java.util.stream.Stream;

public interface ResidentUpdateJmsQueueProducer {

    void sendAll(Stream<ResidentUpdateQueueDto> residentUpdateDtoStream);

}
