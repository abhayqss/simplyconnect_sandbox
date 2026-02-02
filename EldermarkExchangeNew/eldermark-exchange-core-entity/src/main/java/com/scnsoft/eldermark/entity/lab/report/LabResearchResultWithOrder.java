package com.scnsoft.eldermark.entity.lab.report;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderReason;

import java.time.Instant;

public class LabResearchResultWithOrder extends LabResearchOrderResultWithClient {
    private String resultCode;
    private String resultValue;

    public LabResearchResultWithOrder(Long id, LabResearchOrderReason reason, Instant specimenDate, Long clientId, String clientFirstName,
                                      String clientLastName, String clientCommunityName, Instant oruReceivedDatetime, String resultCode, String resultValue) {
        super(id, reason, specimenDate, clientId, clientFirstName, clientLastName, clientCommunityName, oruReceivedDatetime);
        this.resultCode = resultCode;
        this.resultValue = resultValue;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }
}
