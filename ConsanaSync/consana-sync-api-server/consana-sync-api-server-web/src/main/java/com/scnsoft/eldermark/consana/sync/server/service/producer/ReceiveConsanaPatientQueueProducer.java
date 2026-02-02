package com.scnsoft.eldermark.consana.sync.server.service.producer;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;

public interface ReceiveConsanaPatientQueueProducer {

    void send(ReceiveConsanaPatientQueueDto receiveConsanaPatientQueueDto);

}
