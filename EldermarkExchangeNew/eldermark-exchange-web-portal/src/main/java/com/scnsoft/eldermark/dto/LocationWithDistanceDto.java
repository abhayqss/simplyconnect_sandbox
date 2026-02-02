package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This DTO is intended to represent location with distance to user
 */
public class LocationWithDistanceDto extends LocationDto {

    @JsonProperty("distanceInMiles")
    private Double distanceInMiles = null;

    @JsonProperty("displayDistanceMiles")
    private String displayDistanceMiles = null;

    public Double getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(Double distanceInMiles) {

        this.distanceInMiles = new BigDecimal(distanceInMiles).setScale(   2, RoundingMode.FLOOR).doubleValue();
    }

    public String getDisplayDistanceMiles() {
        return displayDistanceMiles;
    }

    public void setDisplayDistanceMiles(String displayDistanceMiles) {
        DecimalFormat df = new DecimalFormat("#.##");
        this.displayDistanceMiles = displayDistanceMiles;
    }
}