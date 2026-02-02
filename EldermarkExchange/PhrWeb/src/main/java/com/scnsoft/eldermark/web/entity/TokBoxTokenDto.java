package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent TokBox tokens
 */
@ApiModel(description = "This DTO is intended to represent TokBox tokens")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-02T10:35:33.724+03:00")
public class TokBoxTokenDto {

    @JsonProperty("token")
    private String token = null;

    @JsonProperty("callerUserId")
    private Long callerUserId = null;

    @JsonProperty("calleeUserId")
    private Long calleeUserId = null;


    /**
     * Access token
     *
     * @return token
     */
    @ApiModelProperty(example = "YWJjMTIzIT8kKiYoKSctPUB-&#x3D;&#x3D;", value = "Access token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Caller user ID
     * minimum: 1
     *
     * @return callerUserId
     */
    @ApiModelProperty(value = "Caller user ID")
    public Long getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(Long callerUserId) {
        this.callerUserId = callerUserId;
    }

    /**
     * Callee user ID
     * minimum: 1
     *
     * @return calleeUserId
     */
    @ApiModelProperty(value = "Callee user ID")
    public Long getCalleeUserId() {
        return calleeUserId;
    }

    public void setCalleeUserId(Long calleeUserId) {
        this.calleeUserId = calleeUserId;
    }

}
