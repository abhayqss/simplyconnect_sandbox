package com.scnsoft.eldermark.consana.sync.client.services.cron.impl;

import com.scnsoft.eldermark.consana.sync.client.services.converters.ResidentUpdateDatabaseToQueueStreamConverter;
import com.scnsoft.eldermark.consana.sync.client.services.queue.database.ResidentUpdateDatabaseQueueService;
import com.scnsoft.eldermark.consana.sync.client.services.cron.ScheduledDatabaseResidentUpdateQueue;
import com.scnsoft.eldermark.consana.sync.client.services.queue.jms.producers.ResidentUpdateJmsQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduledDatabaseResidentUpdateQueueImpl implements ScheduledDatabaseResidentUpdateQueue {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledDatabaseResidentUpdateQueueImpl.class);

    private final ResidentUpdateDatabaseQueueService residentUpdateQueueService;
    private final ResidentUpdateDatabaseToQueueStreamConverter converter;
    private final ResidentUpdateJmsQueueProducer residentUpdateJmsQueueProducer;

    @Autowired
    public ScheduledDatabaseResidentUpdateQueueImpl(ResidentUpdateDatabaseQueueService residentUpdateQueueService, ResidentUpdateDatabaseToQueueStreamConverter converter, ResidentUpdateJmsQueueProducer residentUpdateJmsQueueProducer) {
        this.residentUpdateQueueService = residentUpdateQueueService;
        this.converter = converter;
        this.residentUpdateJmsQueueProducer = residentUpdateJmsQueueProducer;
    }

    @Override
    @Scheduled(cron = "${database.queue.residentUpdate.cron}")
    public void processBatchFromQueue() {
        logger.info("Processing batch.");
        var batchStream = residentUpdateQueueService.dequeueBatch();
        var transformedBatchStream = converter.apply(batchStream);
        residentUpdateJmsQueueProducer.sendAll(transformedBatchStream);
        logger.info("Batch processed.");
    }
}
