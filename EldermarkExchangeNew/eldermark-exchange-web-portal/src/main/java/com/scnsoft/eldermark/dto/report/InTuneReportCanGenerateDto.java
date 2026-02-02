package com.scnsoft.eldermark.dto.report;

public class InTuneReportCanGenerateDto {

    private boolean value;

    private String reasonCode;
    private String reasonText;

    public InTuneReportCanGenerateDto(boolean value) {
        this.value = value;
    }

    public InTuneReportCanGenerateDto(boolean value, String reasonCode, String reasonText) {
        this.value = value;
        this.reasonCode = reasonCode;
        this.reasonText = reasonText;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }
}
