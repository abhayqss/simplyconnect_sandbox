package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T11:44:53.445-03:00")
public class TelecomsDto {

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("workPhone")
    private String workPhone = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("flowId")
    private String flowId = null;


    /**
    * phone number
    *
    * @return phone
    */
   
    @ApiModelProperty(example = "+6458765432", value = "phone number")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
    * work phone number
    *
    * @return workPhone
    */
   
    @ApiModelProperty(example = "+6458765432", value = "work phone number")
    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    /**
    * email address
    *
    * @return email
    */
   
    @ApiModelProperty(example = "house.md@test.com", value = "email address")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
    * registration flow guid
    *
    * @return flowId
    */
   
    @ApiModelProperty(value = "registration flow guid")
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

}
