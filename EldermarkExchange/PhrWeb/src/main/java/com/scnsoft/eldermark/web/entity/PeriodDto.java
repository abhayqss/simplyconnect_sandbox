package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T18:32:29.103+03:00")
public class PeriodDto {

  @JsonProperty("startDate")
  private Long startDate = null;

  @JsonProperty("startDateStr")
  private String startDateStr = null;

  @JsonProperty("endDate")
  private Long endDate = null;

  @JsonProperty("endDateStr")
  private String endDateStr = null;


  @ApiModelProperty(example = "1336338000000")
  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }


}
