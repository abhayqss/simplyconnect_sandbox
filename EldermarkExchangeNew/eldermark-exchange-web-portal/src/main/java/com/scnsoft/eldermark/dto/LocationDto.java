package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationDto {

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("longitude")
    private Double longitude = null;

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
}
