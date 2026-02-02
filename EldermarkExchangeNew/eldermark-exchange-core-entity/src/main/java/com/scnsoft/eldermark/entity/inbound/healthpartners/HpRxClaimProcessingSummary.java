package com.scnsoft.eldermark.entity.inbound.healthpartners;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

public class HpRxClaimProcessingSummary extends HpRecordProcessingSummary {

    private String claimNO;
    private int lineNumber;

    @JsonView({ProcessingSummary.LocalView.class})
    private boolean duplicate;

    @JsonView(ProcessingSummary.LocalView.class)
    private boolean processedAsAdjustment;

    @JsonView(ProcessingSummary.LocalView.class)
    private Long medicationDispenseId;

    public String getClaimNO() {
        return claimNO;
    }

    public void setClaimNO(String claimNO) {
        this.claimNO = claimNO;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public boolean isProcessedAsAdjustment() {
        return processedAsAdjustment;
    }

    public void setProcessedAsAdjustment(boolean processedAsAdjustment) {
        this.processedAsAdjustment = processedAsAdjustment;
    }

    public Long getMedicationDispenseId() {
        return medicationDispenseId;
    }

    public void setMedicationDispenseId(Long medicationDispenseId) {
        this.medicationDispenseId = medicationDispenseId;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return duplicate || processedAsAdjustment || medicationDispenseId != null;
    }

    @Override
    protected String buildWarnMessage() {
        return "Rx claim could not be processed";
    }
}
