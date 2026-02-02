package com.scnsoft.eldermark.consana.sync.client.services.senders;

import com.scnsoft.eldermark.consana.sync.client.model.ConsanaEventCreatedApiDto;
import com.scnsoft.eldermark.consana.sync.client.model.ConsanaSyncApiDto;

public interface ConsanaSyncApiSender {

    void sendSyncNotification(ConsanaSyncApiDto consanaPatientUpdateApiDto);

    void sendEvent(ConsanaEventCreatedApiDto consanaEventCreatedApiDto);
}
