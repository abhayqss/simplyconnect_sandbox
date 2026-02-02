package com.scnsoft.exchange.audit.model;

import java.util.Date;

public class DataSyncStatsDto implements ReportDto {
    private long iterationNumber;
    private Date started;
    private Date completed;
    private String databaseName;
    private String syncServiceName;
    private String duration;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public long getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(long iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public String getStarted() {
        return convertToString(started);
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public String getCompleted() {
        return (completed == null)? "Not completed" : completed.toString();
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSyncServiceName() {
        return syncServiceName;
    }

    public void setSyncServiceName(String syncServiceName) {
        this.syncServiceName = syncServiceName;
    }

    private static String convertToString(Object obj) {
        return (obj == null)? "" : obj.toString();
    }
}
