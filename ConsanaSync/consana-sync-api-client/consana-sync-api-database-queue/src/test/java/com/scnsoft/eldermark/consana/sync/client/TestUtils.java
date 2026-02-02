package com.scnsoft.eldermark.consana.sync.client;

import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;

import java.time.Instant;

public class TestUtils {

    private TestUtils() {
    }

    public static ResidentUpdateDatabaseQueueBody buildResidentUpdateDatabaseQueueBody() {
        return new ResidentUpdateDatabaseQueueBody();
    }

    public static ResidentUpdateDatabaseQueueBody buildResidentUpdateDatabaseQueueBody(Long residentId,
                                                                                       ResidentUpdateType residentUpdateType,
                                                                                       Long updateTime) {
        return new ResidentUpdateDatabaseQueueBody(residentId, residentUpdateType, updateTime);
    }


}
