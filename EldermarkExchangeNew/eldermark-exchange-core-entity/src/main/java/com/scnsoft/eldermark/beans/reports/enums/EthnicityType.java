package com.scnsoft.eldermark.beans.reports.enums;

public enum EthnicityType {

    LATINO("Hispanic/Latino", 1),
    NOT_LATINO("Not Hispanic/Latino", 2),
    INFO_NOT_COLLECTED(" Information not collected", 77),
    INDIVIDUAL_REFUSED("Individual refused", 88),
    INDIVIDUAL_DOES_NOT_KNOW("Individual does not know", 99);

    EthnicityType(String description, Integer code){
        this.description = description;
        this.code = code;
    }

    String description;

    Integer code;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
