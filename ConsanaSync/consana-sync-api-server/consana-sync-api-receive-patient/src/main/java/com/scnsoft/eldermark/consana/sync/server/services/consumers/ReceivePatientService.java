package com.scnsoft.eldermark.consana.sync.server.services.consumers;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;

public interface ReceivePatientService {

    void receive(ReceiveConsanaPatientQueueDto patientDto);
}
