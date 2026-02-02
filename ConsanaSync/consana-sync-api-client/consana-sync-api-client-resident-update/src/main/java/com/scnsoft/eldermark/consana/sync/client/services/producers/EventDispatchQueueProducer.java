package com.scnsoft.eldermark.consana.sync.client.services.producers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface EventDispatchQueueProducer {

    void sendAll(Collection<ConsanaEventCreatedQueueDto> stream);
}
