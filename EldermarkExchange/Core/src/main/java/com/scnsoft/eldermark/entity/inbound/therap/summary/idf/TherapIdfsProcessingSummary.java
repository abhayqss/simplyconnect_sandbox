package com.scnsoft.eldermark.entity.inbound.therap.summary.idf;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntitiesProcessingSummary;

public class TherapIdfsProcessingSummary extends TherapEntitiesProcessingSummary<TherapIdfFileProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "IDF_DETAIL";
    }
}
