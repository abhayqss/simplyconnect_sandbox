package com.scnsoft.eldermark.consana.sync.client.services.consumers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.EventCreatedQueueDto;

public interface EventCreatedQueueConsumer {

    void consume(EventCreatedQueueDto eventUpdateDto);
}
