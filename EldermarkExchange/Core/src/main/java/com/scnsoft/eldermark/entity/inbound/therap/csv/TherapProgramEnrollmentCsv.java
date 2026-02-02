package com.scnsoft.eldermark.entity.inbound.therap.csv;

import com.opencsv.bean.CsvBindByName;

public class TherapProgramEnrollmentCsv {

    @CsvBindByName(column = "IDFFORMID")
    private String idFormId;

    @CsvBindByName(column = "PROVIDERCODE")
    private String providerCode;

    @CsvBindByName(column = "PGMNAME")
    private String pgmName;

    @CsvBindByName(column = "PGMID")
    private String pgmId;

    @CsvBindByName(column = "PGMSTATUS")
    private String pgmStatus;

    @CsvBindByName(column = "PGMTYPE")
    private String pgmType;

    @CsvBindByName(column = "PGMTYPETYPE")
    private String pgmTypeType;

    @CsvBindByName(column = "PGMCOSTCENTERNUMBER")
    private String pgmCostCenterNumber;

    @CsvBindByName(column = "PGMTABSID")
    private String pgmTabsId;

    @CsvBindByName(column = "PGMCODE")
    private String pgmCode;

    @CsvBindByName(column = "PGMCAPACITY")
    private String pgmCapacity;

    @CsvBindByName(column = "ENROLLMENTDATE")
    private String enrollmentDate;

    @CsvBindByName(column = "DISCHARGEDATE")
    private String dischargeDate;

    @CsvBindByName(column = "PGMPRICONTACTNAME")
    private String pgmPriContactName;

    @CsvBindByName(column = "PGMPRICONTACTPHONE1")
    private String pgmPriContactPhone1;

    @CsvBindByName(column = "PGMPRICONTACTPHONE1EXT")
    private String pgmPriContactPhone1Ext;

    @CsvBindByName(column = "PGMPRICONTACTPHONE2")
    private String pgmPriContactPhone2;

    @CsvBindByName(column = "PGMPRICONTACTPHONE2EXT")
    private String pgmPriContactPhone2Ext;

    @CsvBindByName(column = "PGMSECCONTACTNAME")
    private String pgmSecContactName;

    @CsvBindByName(column = "PGMSECCONTACTPHONE1")
    private String pgmSecContactPhone1;

    @CsvBindByName(column = "PGMSECCONTACTPHONE1EXT")
    private String pgmSecContactPhone1Ext;

    @CsvBindByName(column = "PGMSECCONTACTPHONE2")
    private String pgmSecContactPhone2;

    @CsvBindByName(column = "PGMSECCONTACTPHONE2EXT")
    private String pgmSecContactPhone2Ext;

    @CsvBindByName(column = "PGMCREATED")
    private String pgmCreated;

    @CsvBindByName(column = "PGMUPDATED")
    private String pgmUpdated;

    @CsvBindByName(column = "SITENAME")
    private String siteName;

    @CsvBindByName(column = "SITEID")
    private String siteId;

    @CsvBindByName(column = "SITECODE")
    private String siteCode;

    @CsvBindByName(column = "SITECOSTCENTERNUMBER")
    private String siteCostCenterNumber;

    @CsvBindByName(column = "SITESTATUS")
    private String siteStatus;

    @CsvBindByName(column = "SITESTREET1")
    private String siteStreet1;

    @CsvBindByName(column = "SITESTREET2")
    private String siteStreet2;

    @CsvBindByName(column = "SITECITY")
    private String siteCity;

    @CsvBindByName(column = "SITESTATE")
    private String siteState;

    @CsvBindByName(column = "SITEZIP")
    private String siteZip;

    @CsvBindByName(column = "SITECOUNTRY")
    private String siteCountry;

    @CsvBindByName(column = "SITELATITUDE")
    private String siteLattitude;

    @CsvBindByName(column = "SITELONGITUDE")
    private String siteLongitude;

    @CsvBindByName(column = "SITEPHONE")
    private String sitePhone;

    @CsvBindByName(column = "SITEPHONEEXT")
    private String sitePhoneExt;

    @CsvBindByName(column = "SITEFAX")
    private String siteFax;

    @CsvBindByName(column = "SITECREATED")
    private String siteCreated;

    @CsvBindByName(column = "SITEUPDATED")
    private String siteUpdated;

    @CsvBindByName(column = "SITETIMEZONE")
    private String siteTimezone;

    public String getIdFormId() {
        return idFormId;
    }

    public void setIdFormId(String idFormId) {
        this.idFormId = idFormId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getPgmName() {
        return pgmName;
    }

    public void setPgmName(String pgmName) {
        this.pgmName = pgmName;
    }

    public String getPgmId() {
        return pgmId;
    }

    public void setPgmId(String pgmId) {
        this.pgmId = pgmId;
    }

    public String getPgmStatus() {
        return pgmStatus;
    }

    public void setPgmStatus(String pgmStatus) {
        this.pgmStatus = pgmStatus;
    }

    public String getPgmType() {
        return pgmType;
    }

    public void setPgmType(String pgmType) {
        this.pgmType = pgmType;
    }

    public String getPgmTypeType() {
        return pgmTypeType;
    }

    public void setPgmTypeType(String pgmTypeType) {
        this.pgmTypeType = pgmTypeType;
    }

    public String getPgmCostCenterNumber() {
        return pgmCostCenterNumber;
    }

    public void setPgmCostCenterNumber(String pgmCostCenterNumber) {
        this.pgmCostCenterNumber = pgmCostCenterNumber;
    }

    public String getPgmTabsId() {
        return pgmTabsId;
    }

    public void setPgmTabsId(String pgmTabsId) {
        this.pgmTabsId = pgmTabsId;
    }

    public String getPgmCode() {
        return pgmCode;
    }

    public void setPgmCode(String pgmCode) {
        this.pgmCode = pgmCode;
    }

    public String getPgmCapacity() {
        return pgmCapacity;
    }

    public void setPgmCapacity(String pgmCapacity) {
        this.pgmCapacity = pgmCapacity;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getPgmPriContactName() {
        return pgmPriContactName;
    }

    public void setPgmPriContactName(String pgmPriContactName) {
        this.pgmPriContactName = pgmPriContactName;
    }

    public String getPgmPriContactPhone1() {
        return pgmPriContactPhone1;
    }

    public void setPgmPriContactPhone1(String pgmPriContactPhone1) {
        this.pgmPriContactPhone1 = pgmPriContactPhone1;
    }

    public String getPgmPriContactPhone1Ext() {
        return pgmPriContactPhone1Ext;
    }

    public void setPgmPriContactPhone1Ext(String pgmPriContactPhone1Ext) {
        this.pgmPriContactPhone1Ext = pgmPriContactPhone1Ext;
    }

    public String getPgmPriContactPhone2() {
        return pgmPriContactPhone2;
    }

    public void setPgmPriContactPhone2(String pgmPriContactPhone2) {
        this.pgmPriContactPhone2 = pgmPriContactPhone2;
    }

    public String getPgmPriContactPhone2Ext() {
        return pgmPriContactPhone2Ext;
    }

    public void setPgmPriContactPhone2Ext(String pgmPriContactPhone2Ext) {
        this.pgmPriContactPhone2Ext = pgmPriContactPhone2Ext;
    }

    public String getPgmSecContactName() {
        return pgmSecContactName;
    }

    public void setPgmSecContactName(String pgmSecContactName) {
        this.pgmSecContactName = pgmSecContactName;
    }

    public String getPgmSecContactPhone1() {
        return pgmSecContactPhone1;
    }

    public void setPgmSecContactPhone1(String pgmSecContactPhone1) {
        this.pgmSecContactPhone1 = pgmSecContactPhone1;
    }

    public String getPgmSecContactPhone1Ext() {
        return pgmSecContactPhone1Ext;
    }

    public void setPgmSecContactPhone1Ext(String pgmSecContactPhone1Ext) {
        this.pgmSecContactPhone1Ext = pgmSecContactPhone1Ext;
    }

    public String getPgmSecContactPhone2() {
        return pgmSecContactPhone2;
    }

    public void setPgmSecContactPhone2(String pgmSecContactPhone2) {
        this.pgmSecContactPhone2 = pgmSecContactPhone2;
    }

    public String getPgmSecContactPhone2Ext() {
        return pgmSecContactPhone2Ext;
    }

    public void setPgmSecContactPhone2Ext(String pgmSecContactPhone2Ext) {
        this.pgmSecContactPhone2Ext = pgmSecContactPhone2Ext;
    }

    public String getPgmCreated() {
        return pgmCreated;
    }

    public void setPgmCreated(String pgmCreated) {
        this.pgmCreated = pgmCreated;
    }

    public String getPgmUpdated() {
        return pgmUpdated;
    }

    public void setPgmUpdated(String pgmUpdated) {
        this.pgmUpdated = pgmUpdated;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteCostCenterNumber() {
        return siteCostCenterNumber;
    }

    public void setSiteCostCenterNumber(String siteCostCenterNumber) {
        this.siteCostCenterNumber = siteCostCenterNumber;
    }

    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public String getSiteStreet1() {
        return siteStreet1;
    }

    public void setSiteStreet1(String siteStreet1) {
        this.siteStreet1 = siteStreet1;
    }

    public String getSiteStreet2() {
        return siteStreet2;
    }

    public void setSiteStreet2(String siteStreet2) {
        this.siteStreet2 = siteStreet2;
    }

    public String getSiteCity() {
        return siteCity;
    }

    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }

    public String getSiteState() {
        return siteState;
    }

    public void setSiteState(String siteState) {
        this.siteState = siteState;
    }

    public String getSiteZip() {
        return siteZip;
    }

    public void setSiteZip(String siteZip) {
        this.siteZip = siteZip;
    }

    public String getSiteCountry() {
        return siteCountry;
    }

    public void setSiteCountry(String siteCountry) {
        this.siteCountry = siteCountry;
    }

    public String getSiteLattitude() {
        return siteLattitude;
    }

    public void setSiteLattitude(String siteLattitude) {
        this.siteLattitude = siteLattitude;
    }

    public String getSiteLongitude() {
        return siteLongitude;
    }

    public void setSiteLongitude(String siteLongitude) {
        this.siteLongitude = siteLongitude;
    }

    public String getSitePhone() {
        return sitePhone;
    }

    public void setSitePhone(String sitePhone) {
        this.sitePhone = sitePhone;
    }

    public String getSitePhoneExt() {
        return sitePhoneExt;
    }

    public void setSitePhoneExt(String sitePhoneExt) {
        this.sitePhoneExt = sitePhoneExt;
    }

    public String getSiteFax() {
        return siteFax;
    }

    public void setSiteFax(String siteFax) {
        this.siteFax = siteFax;
    }

    public String getSiteCreated() {
        return siteCreated;
    }

    public void setSiteCreated(String siteCreated) {
        this.siteCreated = siteCreated;
    }

    public String getSiteUpdated() {
        return siteUpdated;
    }

    public void setSiteUpdated(String siteUpdated) {
        this.siteUpdated = siteUpdated;
    }

    public String getSiteTimezone() {
        return siteTimezone;
    }

    public void setSiteTimezone(String siteTimezone) {
        this.siteTimezone = siteTimezone;
    }

    @Override
    public String toString() {
        return "TherapProgramEnrollmentCsv{" +
                "idFormId='" + idFormId + '\'' +
                ", providerCode='" + providerCode + '\'' +
                ", pgmName='" + pgmName + '\'' +
                ", pgmId='" + pgmId + '\'' +
                ", pgmStatus='" + pgmStatus + '\'' +
                ", pgmType='" + pgmType + '\'' +
                ", pgmTypeType='" + pgmTypeType + '\'' +
                ", pgmCostCenterNumber='" + pgmCostCenterNumber + '\'' +
                ", pgmTabsId='" + pgmTabsId + '\'' +
                ", pgmCode='" + pgmCode + '\'' +
                ", pgmCapacity='" + pgmCapacity + '\'' +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", dischargeDate='" + dischargeDate + '\'' +
                ", pgmPriContactName='" + pgmPriContactName + '\'' +
                ", pgmPriContactPhone1='" + pgmPriContactPhone1 + '\'' +
                ", pgmPriContactPhone1Ext='" + pgmPriContactPhone1Ext + '\'' +
                ", pgmPriContactPhone2='" + pgmPriContactPhone2 + '\'' +
                ", pgmPriContactPhone2Ext='" + pgmPriContactPhone2Ext + '\'' +
                ", pgmSecContactName='" + pgmSecContactName + '\'' +
                ", pgmSecContactPhone1='" + pgmSecContactPhone1 + '\'' +
                ", pgmSecContactPhone1Ext='" + pgmSecContactPhone1Ext + '\'' +
                ", pgmSecContactPhone2='" + pgmSecContactPhone2 + '\'' +
                ", pgmSecContactPhone2Ext='" + pgmSecContactPhone2Ext + '\'' +
                ", pgmCreated='" + pgmCreated + '\'' +
                ", pgmUpdated='" + pgmUpdated + '\'' +
                ", siteName='" + siteName + '\'' +
                ", siteId='" + siteId + '\'' +
                ", siteCode='" + siteCode + '\'' +
                ", siteCostCenterNumber='" + siteCostCenterNumber + '\'' +
                ", siteStatus='" + siteStatus + '\'' +
                ", siteStreet1='" + siteStreet1 + '\'' +
                ", siteStreet2='" + siteStreet2 + '\'' +
                ", siteCity='" + siteCity + '\'' +
                ", siteState='" + siteState + '\'' +
                ", siteZip='" + siteZip + '\'' +
                ", siteCountry='" + siteCountry + '\'' +
                ", siteLattitude='" + siteLattitude + '\'' +
                ", siteLongitude='" + siteLongitude + '\'' +
                ", sitePhone='" + sitePhone + '\'' +
                ", sitePhoneExt='" + sitePhoneExt + '\'' +
                ", siteFax='" + siteFax + '\'' +
                ", siteCreated='" + siteCreated + '\'' +
                ", siteUpdated='" + siteUpdated + '\'' +
                ", siteTimezone='" + siteTimezone + '\'' +
                '}';
    }
}
