package com.scnsoft.eldermark.service.rxnorm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NDCStatus {
    private String status;
    @JsonProperty("rxcui")
    private String code;
    @JsonProperty("conceptName")
    private String displayName;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
