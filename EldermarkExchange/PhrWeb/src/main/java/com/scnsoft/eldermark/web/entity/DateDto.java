package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T18:32:29.103+03:00")
public class DateDto {

  @JsonProperty("dateTime")
  private Long dateTime = null;

  @JsonProperty("dateTimeStr")
  private String dateTimeStr = null;


  @ApiModelProperty(example = "1336338000000")
  public Long getDateTime() {
    return dateTime;
  }

  public void setDateTime(Long dateTime) {
    this.dateTime = dateTime;
  }

  public String getDateTimeStr() {
    return dateTimeStr;
  }

  public void setDateTimeStr(String dateTimeStr) {
    this.dateTimeStr = dateTimeStr;
  }

}
