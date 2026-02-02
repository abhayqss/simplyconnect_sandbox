package com.scnsoft.eldermark.services.consana;

import com.scnsoft.eldermark.services.consana.model.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;

import java.util.Set;

public interface ResidentUpdateQueueProducer {

    boolean putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto);

    boolean putToResidentUpdateQueue(Long residentId, Set<ResidentUpdateType> updateTypes);

    boolean putToResidentUpdateQueue(Long residentId, ResidentUpdateType updateType);

}
