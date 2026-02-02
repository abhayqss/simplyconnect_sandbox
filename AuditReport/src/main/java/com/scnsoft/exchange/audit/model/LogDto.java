package com.scnsoft.exchange.audit.model;

import java.util.Date;

public class LogDto implements ReportDto {
    private Long id;
    private Date date;
    private String action;
    private String ipAddress;
    private Long employeeId;
    private String employeeLogin;
    private Long residentId;
    private String residentFirstName;
    private String residentLastName;
    private Long documentId;
    private String documentTitle;
    private Long databaseId;
    private String databaseName;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeLogin() {
        return employeeLogin;
    }

    public void setEmployeeLogin(String employeeLogin) {
        this.employeeLogin = employeeLogin;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getResidentFirstName() {
        return residentFirstName;
    }

    public void setResidentFirstName(String residentFirstName) {
        this.residentFirstName = residentFirstName;
    }

    public String getResidentLastName() {
        return residentLastName;
    }

    public void setResidentLastName(String residentLastName) {
        this.residentLastName = residentLastName;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentName) {
        this.documentTitle = documentName;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    private static String convertToString(Object obj) {
        return (obj == null)? "" : obj.toString();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
