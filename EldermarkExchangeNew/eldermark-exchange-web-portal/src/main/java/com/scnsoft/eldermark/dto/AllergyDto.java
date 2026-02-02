package com.scnsoft.eldermark.dto;

public class AllergyDto extends AllergyListItemDto {
    private String type;
    private String severity;
    private String statusName;
    private String statusTitle;
    private Long stoppedDate;
    private String dataSource;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Long stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
