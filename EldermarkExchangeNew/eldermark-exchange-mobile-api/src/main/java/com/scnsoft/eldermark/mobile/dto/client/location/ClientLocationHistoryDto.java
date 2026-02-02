package com.scnsoft.eldermark.mobile.dto.client.location;

public class ClientLocationHistoryDto extends ClientLocationHistoryListItemDto {

    private String reportedByFirstName;
    private String reportedByLastName;

    public String getReportedByFirstName() {
        return reportedByFirstName;
    }

    public void setReportedByFirstName(String reportedByFirstName) {
        this.reportedByFirstName = reportedByFirstName;
    }

    public String getReportedByLastName() {
        return reportedByLastName;
    }

    public void setReportedByLastName(String reportedByLastName) {
        this.reportedByLastName = reportedByLastName;
    }
}
