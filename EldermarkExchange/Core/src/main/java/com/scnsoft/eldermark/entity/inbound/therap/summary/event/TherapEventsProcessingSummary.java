package com.scnsoft.eldermark.entity.inbound.therap.summary.event;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntitiesProcessingSummary;

public class TherapEventsProcessingSummary extends TherapEntitiesProcessingSummary<TherapEventFileProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "GER_EVENT";
    }

}
