package com.scnsoft.eldermark.service.inbound.philips;

import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventCSV;

public interface PhilipsEventService {

    Event createEvent(PhilipsEventCSV philipsEventCSV);
}
