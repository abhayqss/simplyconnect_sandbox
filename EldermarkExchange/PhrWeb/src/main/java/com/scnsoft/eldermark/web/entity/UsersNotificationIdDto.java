package com.scnsoft.eldermark.web.entity;

import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "This DTO is intended to represent OpenTok user")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-02T10:35:33.724+03:00")
public class UsersNotificationIdDto {
    
    @JsonProperty("calleeList")
    private List<UserDetailDto> calleeList = null;

    public List<UserDetailDto> getCalleeList() {
        return calleeList;
    }

    public void setCalleeList(List<UserDetailDto> calleeList) {
        this.calleeList = calleeList;
    }

       
}
