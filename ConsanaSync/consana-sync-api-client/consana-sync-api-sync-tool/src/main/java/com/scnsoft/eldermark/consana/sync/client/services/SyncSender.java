package com.scnsoft.eldermark.consana.sync.client.services;

import java.util.List;

public interface SyncSender {

    void sendSyncNotifications(List<Long> communityIds);

}
