package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.services.processors.SyncProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SyncServiceImpl implements SyncService {

    @Autowired
    private List<SyncProcessor> processors;

    @Override
    public void process(ApplicationSyncContext applicationSyncContext) {
        for (var processor : processors) {
            if (processor.isApplies(applicationSyncContext)) {
                processor.process(applicationSyncContext);
                return;
            }
        }
    }
}
