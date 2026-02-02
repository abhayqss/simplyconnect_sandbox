package com.scnsoft.eldermark.entity.inbound.therap.summary.event;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityFileProcessingSummary;

public class TherapEventFileProcessingSummary extends TherapEntityFileProcessingSummary<TherapEventRecordProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "GER_EVENT";
    }
}
