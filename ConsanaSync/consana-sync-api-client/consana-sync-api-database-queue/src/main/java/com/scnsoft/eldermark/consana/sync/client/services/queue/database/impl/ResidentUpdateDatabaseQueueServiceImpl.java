package com.scnsoft.eldermark.consana.sync.client.services.queue.database.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentUpdateQueueDao;
import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.services.queue.database.ResidentUpdateDatabaseQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@Transactional
public class ResidentUpdateDatabaseQueueServiceImpl implements ResidentUpdateDatabaseQueueService {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateDatabaseQueueServiceImpl.class);

    @Value("${database.queue.residentUpdate.batchSize}")
    private int batchSize;

    private final ResidentUpdateQueueDao residentUpdateQueueDao;

    @Autowired
    public ResidentUpdateDatabaseQueueServiceImpl(ResidentUpdateQueueDao residentUpdateQueueDao) {
        this.residentUpdateQueueDao = residentUpdateQueueDao;
    }

    @Override
    @NonNull
    public Stream<ResidentUpdateDatabaseQueueBody> dequeueBatch() {
        return residentUpdateQueueDao.deque(batchSize).stream().peek(r -> logger.debug("fetched {}", r));
    }
}
