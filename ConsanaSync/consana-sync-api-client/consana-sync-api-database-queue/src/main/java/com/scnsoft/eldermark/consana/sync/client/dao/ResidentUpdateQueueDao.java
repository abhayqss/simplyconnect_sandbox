package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;

import java.util.List;

public interface ResidentUpdateQueueDao {

    List<ResidentUpdateDatabaseQueueBody> deque(int batchSize);

}
