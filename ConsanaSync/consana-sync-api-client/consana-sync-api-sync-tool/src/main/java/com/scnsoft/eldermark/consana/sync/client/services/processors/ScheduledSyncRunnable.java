package com.scnsoft.eldermark.consana.sync.client.services.processors;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.entities.BaseReadOnlyEntity;
import com.scnsoft.eldermark.consana.sync.client.services.CommunityService;
import com.scnsoft.eldermark.consana.sync.client.services.SyncSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScheduledSyncRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledSyncRunnable.class);

    @Autowired
    private SyncSender syncSender;

    @Autowired
    private CommunityService communityService;

    private Map<Long, Instant> dataSyncUpdateTimes;
    private ApplicationSyncContext syncContext;


    public void setDataSyncUpdateTimes(Map<Long, Instant> dataSyncUpdateTimes) {
        this.dataSyncUpdateTimes = dataSyncUpdateTimes;
    }

    public void setSyncContext(ApplicationSyncContext syncContext) {
        this.syncContext = syncContext;
    }

    @Override
    @Transactional(readOnly = true)
    public void run() {
        logger.info("Scheduled sync tick...");
        List<Long> ids;
        if (syncContext.isCheckDataSyncStatus()) {
            var communities = communityService.findAllByIds(syncContext.getCommunityIds());
            ids = communities.stream()
                    .filter(c -> {
                        var lastSync = c.getDatabase().getLastSyncSuccessDate();
                        if (lastSync == null) {
                            logger.info("Last sync for community [{}] is null", c.getId());
                            return true;
                        }
                        if (!dataSyncUpdateTimes.containsKey(c.getId()) || dataSyncUpdateTimes.get(c.getId()).isBefore(lastSync)) {
                            logger.info("Detected last sync date change for community [{}}]: current is {}, previous is {}",
                                    c.getId(), lastSync, dataSyncUpdateTimes.getOrDefault(c.getId(), null));
                            dataSyncUpdateTimes.put(c.getId(), lastSync);
                            return true;
                        }
                        logger.info("Last sync for community [{}] is {} and haven't updated since last checked", c.getId(),
                                dataSyncUpdateTimes.get(c.getId()));
                        return false;
                    })
                    .map(BaseReadOnlyEntity::getId)
                    .collect(Collectors.toList());
        } else {
            ids = syncContext.getCommunityIds();
        }

        syncSender.sendSyncNotifications(ids);
        logger.info("Scheduled sync done...");

    }
}
