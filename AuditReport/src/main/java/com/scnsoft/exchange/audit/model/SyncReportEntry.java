package com.scnsoft.exchange.audit.model;


import java.util.Date;

public class SyncReportEntry implements ReportDto {
    private String databaseName;
    private Date lastSyncDate;
    private long errorCount;
    private boolean status;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStatusAsString() {
        return status ? "Yes" : "No";
    }

    public String getLastSyncDate() {
        return convertToString(lastSyncDate);
    }

    public void setLastSyncDate(Date lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    private static String convertToString(Object obj) {
        return (obj == null)? "" : obj.toString();
    }
}
