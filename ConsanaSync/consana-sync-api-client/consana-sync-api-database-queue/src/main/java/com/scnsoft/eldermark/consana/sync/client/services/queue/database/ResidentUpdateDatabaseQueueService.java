package com.scnsoft.eldermark.consana.sync.client.services.queue.database;

import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Stream;

public interface ResidentUpdateDatabaseQueueService {

    @NonNull
    Stream<ResidentUpdateDatabaseQueueBody> dequeueBatch();

}
