package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentDao;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import com.scnsoft.eldermark.consana.sync.client.services.producers.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Service
public class SyncSenderImpl implements SyncSender {

    private static final Logger logger = LoggerFactory.getLogger(SyncSenderImpl.class);

    private final SqlServerService sqlServerService;
    private final ResidentDao residentDao;
    private final ResidentUpdateQueueProducer residentUpdateQueueProducer;

    @Autowired
    public SyncSenderImpl(SqlServerService sqlServerService, ResidentDao residentDao,
                          ResidentUpdateQueueProducer residentUpdateQueueProducer) {
        this.sqlServerService = sqlServerService;
        this.residentDao = residentDao;
        this.residentUpdateQueueProducer = residentUpdateQueueProducer;
    }

    @Override
    @Transactional(readOnly = true)
    public void sendSyncNotifications(List<Long> communityIds) {
        logger.info("Will send updates for communitues [{}]", communityIds);
        sqlServerService.openKey();
        communityIds.forEach(this::send);
    }

    private void send(Long communityId) {
        logger.info("Fetching residents in community {}...", communityId);
        var residents = residentDao.findAllByFacilityId(communityId);
        logger.info("Fetched {} residents", residents.size());

        residents.forEach(r -> residentUpdateQueueProducer
                .putToResidentUpdateQueue(r.getId(), EnumSet.allOf(ResidentUpdateType.class)));

        logger.info("Finished processing of community {}", communityId);
    }
}
