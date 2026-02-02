package com.scnsoft.eldermark.entity.inbound.therap.summary.idf;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityFileProcessingSummary;

public class TherapIdfFileProcessingSummary extends TherapEntityFileProcessingSummary<TherapIdfRecordProcessingSummary> {

    @Override
    protected String getEntityType() {
        return "IDF_DETAIL";
    }
}
