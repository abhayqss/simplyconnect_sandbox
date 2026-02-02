package com.scnsoft.eldermark.consana.sync.client.services.producers;


import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;

import java.util.Set;

public interface ResidentUpdateQueueProducer {

    void putToResidentUpdateQueue(Long residentId, Set<ResidentUpdateType> updateTypes);

}
