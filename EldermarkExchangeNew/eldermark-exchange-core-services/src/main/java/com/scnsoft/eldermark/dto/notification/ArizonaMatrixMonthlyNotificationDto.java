package com.scnsoft.eldermark.dto.notification;

public class ArizonaMatrixMonthlyNotificationDto {
    private String employeeName;
    private String employeeEmail;
    private String reportUrl;

    public ArizonaMatrixMonthlyNotificationDto(String employeeName, String employeeEmail, String reportUrl) {
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.reportUrl = reportUrl;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
