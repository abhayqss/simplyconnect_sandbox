package com.scnsoft.eldermark.consana.sync.client.services.consumers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;

public interface EventDispatchQueueConsumer {

    void consume(ConsanaEventCreatedQueueDto consanaEventCreatedQueueDto);

}
