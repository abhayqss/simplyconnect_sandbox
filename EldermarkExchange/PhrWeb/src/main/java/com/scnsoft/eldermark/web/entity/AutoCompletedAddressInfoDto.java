package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent autocompleted address
 */
@ApiModel(description = "This DTO is intended to represent autocompleted address")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-29T18:29:54.199+03:00")
public class AutoCompletedAddressInfoDto {

    @JsonProperty("value")
    private String value = null;

    @JsonProperty("locationType")
    private String locationType = null;


    /**
     * Address
     * @return value
     */
    @ApiModelProperty(example = "Schenectady, NY 12345, United States", value = "Address")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Autocompleted calue type
     * @return locationType
     */
    @ApiModelProperty(example = "CITY", value = "Autocompleted calue type")
    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

}

