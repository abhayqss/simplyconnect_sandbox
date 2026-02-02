package com.scnsoft.eldermark.consana.sync.client.services.processors;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;

public interface SyncProcessor {

    void process(ApplicationSyncContext syncContext);

    boolean isApplies(ApplicationSyncContext syncContext);
}
