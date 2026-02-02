package com.scnsoft.eldermark.dto.client;

public class BillingItemDto {

    private String insurance;
    private String plan;
    private String groupNumber;
    private String policyNumber;
    private String insurancePlanConcat;

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getInsurancePlanConcat() {
        return insurancePlanConcat;
    }

    public void setInsurancePlanConcat(String insurancePlanConcat) {
        this.insurancePlanConcat = insurancePlanConcat;
    }

}
