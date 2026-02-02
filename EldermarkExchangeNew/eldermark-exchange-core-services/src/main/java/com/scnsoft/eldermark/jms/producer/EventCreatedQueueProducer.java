package com.scnsoft.eldermark.jms.producer;

public interface EventCreatedQueueProducer {

    void putToEventCreatedQueue(Long eventId);
}
