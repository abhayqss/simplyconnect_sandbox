package com.scnsoft.eldermark.beans.reports.model.ecmtracking;

import java.time.Instant;

public class EcmTrackingReportInsuranceAuthorizationDto {
    private Instant startDate;
    private Instant endDate;
    private String number;

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
