package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T19:07:48.729+03:00")
public class MedicationInfoDto {

  @JsonProperty("directions")
  private String directions = null;

  @JsonProperty("indications")
  private List<String> indications = new ArrayList<String>();

  @JsonProperty("medicationName")
  private String medicationName = null;

  @JsonProperty("startedDate")
  private Long startedDate = null;

  @JsonProperty("startedDateStr")
  private String startedDateStr = null;

  @JsonProperty("stoppedDate")
  private Long stoppedDate = null;

  @JsonProperty("stoppedDateStr")
  private String stoppedDateStr = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("dataSource")
  private DataSourceDto dataSource = null;


  @ApiModelProperty(example = "Give 30 ml by mouth daily as needed for Constipation. Also has routine order.")
  public String getDirections() {
    return directions;
  }

  public void setDirections(String directions) {
    this.directions = directions;
  }

  public MedicationInfoDto addIndicationsItem(String indicationsItem) {
    this.indications.add(indicationsItem);
    return this;
  }

  public List<String> getIndications() {
    return indications;
  }

  public void setIndications(List<String> indications) {
    this.indications = indications;
  }

  @ApiModelProperty(example = "Milk of Magnesia 7.75 % SUSP")
  public String getMedicationName() {
    return medicationName;
  }

  public void setMedicationName(String medicationName) {
    this.medicationName = medicationName;
  }

  @ApiModelProperty(example = "1326862800000")
  public Long getStartedDate() {
    return startedDate;
  }

  public void setStartedDate(Long startedDate) {
    this.startedDate = startedDate;
  }

  public String getStartedDateStr() {
    return startedDateStr;
  }

  public void setStartedDateStr(String startedDateStr) {
    this.startedDateStr = startedDateStr;
  }

  public Long getStoppedDate() {
    return stoppedDate;
  }

  public void setStoppedDate(Long stoppedDate) {
    this.stoppedDate = stoppedDate;
  }

  public String getStoppedDateStr() {
    return stoppedDateStr;
  }

  public void setStoppedDateStr(String stoppedDateStr) {
    this.stoppedDateStr = stoppedDateStr;
  }

  @ApiModelProperty(example = "completed")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public DataSourceDto getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSourceDto dataSource) {
    this.dataSource = dataSource;
  }

}

