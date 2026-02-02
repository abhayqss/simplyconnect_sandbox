package com.scnsoft.eldermark.shared.marketplace;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This DTO is intended to represent location with distance to user
 */
public class LocationDto  {

    @JsonProperty("distanceInMiles")
    private Double distanceInMiles = null;

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("longitude")
    private Double longitude = null;

    @JsonProperty("displayDistanceMiles")
    private String displayDistanceMiles = null;

    public Double getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(Double distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDisplayDistanceMiles() {
        return displayDistanceMiles;
    }

    public void setDisplayDistanceMiles(String displayDistanceMiles) {
        this.displayDistanceMiles = displayDistanceMiles;
    }
}