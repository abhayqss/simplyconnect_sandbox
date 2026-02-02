package com.scnsoft.exchange.audit.model;

import java.util.Date;

public class DataSyncLogDto implements ReportDto {
    private Long id;
    private Date date;
    private String description;
    private String tableName;
    private String stackTrace;
    private String type;
    private String databaseName;
    private Long iterationNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return convertToString(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Long getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(Long iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    private static String convertToString(Object obj) {
        return (obj == null)? "" : obj.toString();
    }
}
