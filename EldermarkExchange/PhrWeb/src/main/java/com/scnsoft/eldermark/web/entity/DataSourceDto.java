package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T18:32:29.103+03:00")
public class DataSourceDto {

  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("residentId")
  private Long residentId = null;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ApiModelProperty(example = "Allina Health")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getResidentId() {
    return residentId;
  }

  public void setResidentId(Long residentId) {
    this.residentId = residentId;
  }

}
