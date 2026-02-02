package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent coordinates
 */
@ApiModel(description = "This DTO is intended to represent coordinates")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-20T16:47:33.250+03:00")
public class CoordinatesDto {

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("longitude")
    private Double longitude = null;


    /**
     * Location of object (latitude).
     * @return latitude
     */
    @ApiModelProperty(value = "Location of object (latitude).")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Location of object (longitude).
     * @return longitude
     */
    @ApiModelProperty(value = "Location of object (longitude).")
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}

