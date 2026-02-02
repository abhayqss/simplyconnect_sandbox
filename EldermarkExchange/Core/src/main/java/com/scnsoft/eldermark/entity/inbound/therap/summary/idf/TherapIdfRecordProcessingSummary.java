package com.scnsoft.eldermark.entity.inbound.therap.summary.idf;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityRecordProcessingSummary;

public class TherapIdfRecordProcessingSummary extends TherapEntityRecordProcessingSummary {

    private String idfFormId;
    private boolean residentAlreadyExisted;

    @JsonView(ProcessingSummary.LocalView.class)
    private Long residentId;

    public String getIdfFormId() {
        return idfFormId;
    }

    public void setIdfFormId(String idfFormId) {
        this.idfFormId = idfFormId;
    }

    public boolean isResidentAlreadyExisted() {
        return residentAlreadyExisted;
    }

    public void setResidentAlreadyExisted(boolean residentAlreadyExisted) {
        this.residentAlreadyExisted = residentAlreadyExisted;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return true;
    }

    @Override
    protected String buildWarnMessage() {
        return "Issues during IDF record processing";
    }
}
