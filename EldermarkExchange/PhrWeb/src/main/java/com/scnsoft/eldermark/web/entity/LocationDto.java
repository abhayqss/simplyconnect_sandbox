package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent location with distance to user
 */
@ApiModel(description = "This DTO is intended to represent location with distance to user")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-15T11:04:46.331+03:00")
public class LocationDto extends CoordinatesDto {

    @JsonProperty("distanceInMiles")
    private Double distanceInMiles = null;

    /**
     * Distance to current user's location.
     * @return distanceInMiles
     */
    @ApiModelProperty(value = "Distance to current user's location.")
    public Double getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(Double distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

}