package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T19:27:42.593+03:00")
public class VitalSignObservationReport {

  @JsonProperty("results")
  private List<VitalSignObservationDto> results = new ArrayList<VitalSignObservationDto>();

  @JsonProperty("unit")
  private String unit = null;

  @JsonProperty("vitalSignType")
  private String vitalSignType;

  @JsonProperty("vitalSignTypeDisplayName")
  private String vitalSignTypeDisplayName;


  public VitalSignObservationReport(String vitalSignType, String vitalSignTypeDisplayName) {
    this.vitalSignType = vitalSignType;
    this.vitalSignTypeDisplayName = vitalSignTypeDisplayName;
  }

  public VitalSignObservationReport addResultsItem(VitalSignObservationDto resultsItem) {
    this.results.add(resultsItem);
    return this;
  }

  public List<VitalSignObservationDto> getResults() {
    return results;
  }

  public void setResults(List<VitalSignObservationDto> results) {
    this.results = results;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Type of Vital Sign to report
   * @return vitalSignType
   */
  @ApiModelProperty(value = "Type of Vital Sign to report")
  public String getVitalSignType() {
    return vitalSignType;
  }

  public void setVitalSignType(String vitalSignType) {
    this.vitalSignType = vitalSignType;
  }

  /**
   * Human readable description of Vital Sign type
   * @return vitalSignTypeDisplayName
   */
  @ApiModelProperty(value = "Human readable description of Vital Sign type")
  public String getVitalSignTypeDisplayName() {
    return vitalSignTypeDisplayName;
  }

  public void setVitalSignTypeDisplayName(String vitalSignTypeDisplayName) {
    this.vitalSignTypeDisplayName = vitalSignTypeDisplayName;
  }

}
