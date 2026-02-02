package com.scnsoft.eldermark.shared;

public class BillingInfo {

    private String insuranceName;

    private String planName;

    private String policyNumber;

    private String groupNumber;

    public BillingInfo(){}

    public BillingInfo( String insuranceName, String planName, String policyNumber, String groupNumber){
        this.insuranceName = insuranceName;
        this.planName = planName;
        this.policyNumber = policyNumber;
        this.groupNumber = groupNumber;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }
}
