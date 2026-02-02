package com.scnsoft.eldermark.consana.sync.client.services.processors;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.services.SyncSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OneTimeSyncProcessor implements SyncProcessor {

    @Autowired
    private SyncSender syncSender;

    @Override
    public void process(ApplicationSyncContext syncContext) {
        syncSender.sendSyncNotifications(syncContext.getCommunityIds());
    }

    @Override
    public boolean isApplies(ApplicationSyncContext syncContext) {
        return syncContext.getSchedule() == 0;
    }
}
