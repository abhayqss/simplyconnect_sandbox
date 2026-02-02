package com.scnsoft.eldermark.consana.sync.client.services.producer;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;

public interface EventDispatchQueueProducer {

    void send(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto);
}
