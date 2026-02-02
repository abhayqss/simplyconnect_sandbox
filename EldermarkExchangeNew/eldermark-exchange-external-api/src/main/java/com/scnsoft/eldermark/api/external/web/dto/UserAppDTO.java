package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-08T15:21:55.757+03:00")
public class UserAppDTO {

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("appName")
    private String appName = null;

    @JsonProperty("type")
    private String type = null;


    /**
     * User id
     * minimum: 1
     *
     * @return userId
     */
    @ApiModelProperty(example = "100", value = "User id")
    @Min(1)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Application name
     *
     * @return appName
     */
    @ApiModelProperty(value = "Application name")
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * User role
     *
     * @return type
     */
    @ApiModelProperty(example = "APPLICATION", value = "User role")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

