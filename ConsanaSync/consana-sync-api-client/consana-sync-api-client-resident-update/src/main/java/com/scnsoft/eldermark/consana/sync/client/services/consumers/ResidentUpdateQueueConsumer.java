package com.scnsoft.eldermark.consana.sync.client.services.consumers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;

import javax.jms.JMSException;

public interface ResidentUpdateQueueConsumer {

    void consume(ResidentUpdateQueueDto message) throws JMSException;

}
