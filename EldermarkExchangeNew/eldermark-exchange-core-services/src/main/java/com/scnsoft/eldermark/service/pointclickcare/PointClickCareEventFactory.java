package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.event.Event;

public interface PointClickCareEventFactory {

    Event createEvent(Client client, String eventTypeCode, String defaultSituation, Long pccAdtRecordId);
}
