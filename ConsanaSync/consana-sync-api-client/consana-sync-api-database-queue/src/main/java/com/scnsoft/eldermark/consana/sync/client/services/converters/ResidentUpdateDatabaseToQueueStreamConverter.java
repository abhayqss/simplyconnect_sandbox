package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;

import java.util.function.Function;
import java.util.stream.Stream;

public interface ResidentUpdateDatabaseToQueueStreamConverter extends Function<Stream<ResidentUpdateDatabaseQueueBody>, Stream<ResidentUpdateQueueDto>> {

}
