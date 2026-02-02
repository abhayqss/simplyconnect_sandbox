package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-18T18:33:44.967+03:00")
public class RegistrationAnswerDto {

    @JsonProperty("hasActiveWebAccount")
    private Boolean hasActiveWebAccount;

    @JsonProperty("flowId")
    private String flowId;

    @ApiModelProperty
    public Boolean getHasActiveWebAccount() {
        return hasActiveWebAccount;
    }

    public void setHasActiveWebAccount(Boolean hasActiveWebAccount) {
        this.hasActiveWebAccount = hasActiveWebAccount;
    }

    @ApiModelProperty
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

}

