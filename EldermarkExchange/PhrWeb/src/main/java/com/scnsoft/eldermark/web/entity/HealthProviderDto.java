package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T18:32:29.103+03:00")
public class HealthProviderDto {

  @JsonProperty("current")
  private Boolean current = null;

  @JsonProperty("providerId")
  private Long providerId = null;

  @JsonProperty("providerName")
  private String providerName = null;

  @JsonProperty("residentId")
  private Long residentId = null;


  public Boolean getCurrent() {
    return current;
  }

  public void setCurrent(Boolean current) {
    this.current = current;
  }

  public Long getProviderId() {
    return providerId;
  }

  public void setProviderId(Long providerId) {
    this.providerId = providerId;
  }

  public String getProviderName() {
    return providerName;
  }

  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }

  public Long getResidentId() {
    return residentId;
  }

  public void setResidentId(Long residentId) {
    this.residentId = residentId;
  }

}
