package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-02T16:19:34.209+03:00")
public class CallActivityDto extends ActivityDto {

  @JsonProperty("callType")
  private ActivityDto.CallType callType = null;
  
  @JsonProperty("duration")
  private Integer duration = null;


  /**
   * Call type
   * @return callType
   */
  @ApiModelProperty(value = "Call type")
  public ActivityDto.CallType getCallType() {
    return callType;
  }

  public void setCallType(ActivityDto.CallType callType) {
    this.callType = callType;
  }

  /**
   * Call duration (in seconds)
   * @return duration
   */
  @ApiModelProperty(value = "Call duration (in seconds)")
  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

}

