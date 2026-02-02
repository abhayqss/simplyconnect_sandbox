package com.scnsoft.eldermark.services.consana;

public interface EventCreatedQueueProducer {

    boolean putToEventCreatedQueue(Long eventId);
}
