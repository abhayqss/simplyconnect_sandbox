package com.scnsoft.eldermark.dto;

public class PhilipsTestDto {
    private Long clientId;
    private String programCode;
    private String subNid;
    private String situation;
    private String outcome;
    private int inServiceDays;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public String getSubNid() {
        return subNid;
    }

    public void setSubNid(String subNid) {
        this.subNid = subNid;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public int getInServiceDays() {
        return inServiceDays;
    }

    public void setInServiceDays(int inServiceDays) {
        this.inServiceDays = inServiceDays;
    }
}
