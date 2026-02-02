package com.scnsoft.eldermark.consana.sync.client.services.consumers;

import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;

public interface PatientDispatchQueueConsumer {

    void consume(ConsanaPatientUpdateQueueDto consanaPatientUpdateDto);

}
