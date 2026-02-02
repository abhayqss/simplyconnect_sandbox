package com.scnsoft.eldermark.services.inbound.therap;

import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapEventCSV;

import java.util.List;

public interface TherapEventService {

    List<Event> createEvents(TherapEventCSV eventCSV, Long idfResidentId);

}
