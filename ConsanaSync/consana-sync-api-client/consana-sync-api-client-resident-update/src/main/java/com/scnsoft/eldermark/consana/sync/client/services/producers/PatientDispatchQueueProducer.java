package com.scnsoft.eldermark.consana.sync.client.services.producers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;

import java.util.Collection;
import java.util.stream.Stream;

public interface PatientDispatchQueueProducer {

    void sendAll(Collection<ConsanaPatientUpdateQueueDto> consanaPatientUpdateDtoStream);
}
