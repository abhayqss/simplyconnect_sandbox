package com.scnsoft.eldermark.consana.sync.client.services.processors;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.services.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScheduledSyncProcessor implements SyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledSyncProcessor.class);

    @Autowired
    private CommunityService communityService;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ObjectFactory<ScheduledSyncRunnable> runnableFactory;

    @Override
    @Transactional(readOnly = true)
    public void process(ApplicationSyncContext syncContext) {
        var runnable = runnableFactory.getObject();
        runnable.setSyncContext(syncContext);

        if (syncContext.isCheckDataSyncStatus()) {
            Map<Long, Instant> dataSyncUpdateTimes = dataSyncUpdateTimes(syncContext);
            runnable.setDataSyncUpdateTimes(dataSyncUpdateTimes);
        }

        logger.info("Scheduling syncs for communities [{}] with {} minutes delay",
                syncContext.getCommunityIds(), syncContext.getSchedule());
        taskScheduler.scheduleWithFixedDelay(runnable, syncContext.getSchedule() * 60 * 1000);
    }

    private Map<Long, Instant> dataSyncUpdateTimes(ApplicationSyncContext syncContext) {
        var result = new ConcurrentHashMap<Long, Instant>(syncContext.getCommunityIds().size());
        if (syncContext.isCheckDataSyncStatusSyncFirst()) {
            return result;
        }

        var communities = communityService.findAllByIds(syncContext.getCommunityIds());

        communities.forEach(c -> {
            if (c.getDatabase().getLastSyncSuccessDate() != null) {
                result.put(c.getId(), c.getDatabase().getLastSyncSuccessDate());
            }
        });
        return result;
    }

    @Override
    public boolean isApplies(ApplicationSyncContext syncContext) {
        return syncContext.getSchedule() > 0;
    }
}
