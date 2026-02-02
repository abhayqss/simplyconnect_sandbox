package com.scnsoft.eldermark.entity.inbound.healthpartners;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

public class HpMedClaimProcessingSummary extends HpRecordProcessingSummary {

    private String claimNO;
    private String diagnosisCode;
    private int lineNumber;

    @JsonView({ProcessingSummary.LocalView.class})
    private boolean duplicate;

    @JsonView({ProcessingSummary.LocalView.class})
    private Long problemObservationId;

    public String getClaimNO() {
        return claimNO;
    }

    public void setClaimNO(String claimNO) {
        this.claimNO = claimNO;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
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

    public Long getProblemObservationId() {
        return problemObservationId;
    }

    public void setProblemObservationId(Long problemObservationId) {
        this.problemObservationId = problemObservationId;
    }


    @Override
    protected boolean shouldSetOkStatus() {
        return duplicate || problemObservationId != null;
    }

    @Override
    protected String buildWarnMessage() {
        return "Medical claim could not be processed";
    }
}
