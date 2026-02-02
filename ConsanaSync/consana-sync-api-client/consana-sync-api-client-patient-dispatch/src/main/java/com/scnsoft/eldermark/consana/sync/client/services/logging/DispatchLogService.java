package com.scnsoft.eldermark.consana.sync.client.services.logging;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaEventCreatedQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;

import java.time.Instant;

public interface DispatchLogService {

    void logSuccess(ConsanaEventCreatedQueueDto dto, Resident resident);

    void logFail(ConsanaEventCreatedQueueDto dto, Resident resident, Exception ex);

    void logInfo(ConsanaEventCreatedQueueDto dto, Resident resident, String info);

    void logSuccess(ConsanaPatientUpdateQueueDto dto);

    void logSuccess(ConsanaPatientUpdateQueueDto dto, Instant wasAlreadySyncedAt);

    void logFail(ConsanaPatientUpdateQueueDto dto, Exception ex);
}
