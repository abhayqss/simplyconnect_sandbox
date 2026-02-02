package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.event.xml.dto.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.event.xml.schema.DeviceEvents;
import com.scnsoft.eldermark.event.xml.schema.Events;

public interface EventsService {

    void processEvents(Events events);

    DeviceEventProcessingResultDto processDeviceEvents(DeviceEvents deviceEvents);
}
