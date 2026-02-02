package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-02T16:19:34.209+03:00")
public class VideoActivityDto extends ActivityDto {

  @JsonProperty("videoType")
  private ActivityDto.CallType videoType = null;
  
  @JsonProperty("duration")
  private Integer duration = null;


  /**
   * Video type
   * @return videoType
   */
  @ApiModelProperty(value = "Video type")
  public ActivityDto.CallType getVideoType() {
    return videoType;
  }

  public void setVideoType(ActivityDto.CallType videoType) {
    this.videoType = videoType;
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

