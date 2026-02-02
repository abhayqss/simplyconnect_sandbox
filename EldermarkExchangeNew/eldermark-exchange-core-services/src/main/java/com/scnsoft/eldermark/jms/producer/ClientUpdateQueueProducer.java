package com.scnsoft.eldermark.jms.producer;

import com.scnsoft.eldermark.jms.dto.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;

import java.util.Set;

public interface ClientUpdateQueueProducer {

    boolean putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto);

    boolean putToResidentUpdateQueue(Long clientId, Set<ResidentUpdateType> updateTypes);

    boolean putToResidentUpdateQueue(Long clientId, ResidentUpdateType updateType);

}
