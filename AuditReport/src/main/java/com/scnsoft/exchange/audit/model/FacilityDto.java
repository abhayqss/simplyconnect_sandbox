package com.scnsoft.exchange.audit.model;


import java.util.Date;

public class FacilityDto implements ReportDto {
    private Long companyId;
    private String companyName;
    private String name;
    private String state;
    private String testingTraining;
    private String salesRegion;
    private Long residentNumber;
    private Date lastSyncDate;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTestingTraining() {
        return testingTraining;
    }

    public void setTestingTraining(String testingTraining) {
        this.testingTraining = testingTraining;
    }

    public String getSalesRegion() {
        return salesRegion;
    }

    public void setSalesRegion(String salesRegion) {
        this.salesRegion = salesRegion;
    }

    public Long getResidentNumber() {
        return residentNumber;
    }

    public void setResidentNumber(Long residentNumber) {
        this.residentNumber = residentNumber;
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
