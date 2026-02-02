package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T18:32:29.103+03:00")
public class PayerInfoDto implements Comparable<PayerInfoDto> {

  @JsonProperty("companyName")
  private String companyName = null;

  @JsonProperty("memberId")
  private String memberId = null;

  @JsonProperty("coveragePeriod")
  private PeriodDto coveragePeriod = null;

  @JsonProperty("dataSource")
  private DataSourceDto dataSource = null;


  @ApiModelProperty(example = "Aetna")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  @ApiModelProperty(example = "6547841")
  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public PeriodDto getCoveragePeriod() {
    return coveragePeriod;
  }

  public void setCoveragePeriod(PeriodDto coveragePeriod) {
    this.coveragePeriod = coveragePeriod;
  }

  public DataSourceDto getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSourceDto dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public int compareTo(PayerInfoDto o) {
    int result = ObjectUtils.compare(this.coveragePeriod.getStartDate(), o.coveragePeriod.getStartDate());
    if (result != 0) return result;
    return ObjectUtils.compare(this.companyName, o.companyName);
  }
}
