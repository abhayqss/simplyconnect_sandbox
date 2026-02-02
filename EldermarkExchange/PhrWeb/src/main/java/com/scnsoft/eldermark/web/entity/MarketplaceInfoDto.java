package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent community details
 */
@ApiModel(description = "This DTO is intended to represent marketplace details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-12T11:34:30.056+03:00")
public class MarketplaceInfoDto extends BasicMarketplaceInfoDto {

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("summary")
    private String summary = null;

    @JsonProperty("allowAppointments")
    private Boolean allowAppointments = null;

    /**
     * Community main phone. It contains a single value.
     * @return phone
     */
    @ApiModelProperty(example = "+375299999999", value = "Community main phone. It contains a single value.")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Marketplace summary. It contains a single value.
     * @return summary
     */
    @ApiModelProperty(value = "Marketplace summary. It contains a single value.")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @ApiModelProperty(value = "")
    public Boolean getAllowAppointments() {
        return allowAppointments;
    }

    public void setAllowAppointments(Boolean allowAppointments) {
        this.allowAppointments = allowAppointments;
    }
}