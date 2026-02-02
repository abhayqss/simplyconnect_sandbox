package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPersonalDetailsDto {
    @JsonProperty("id")
    private Long id = 0L;
    
    @JsonProperty("userId")
    private Long userId = null;
    
    @JsonProperty("userFullName")
    private String userFullName = null;
    
    @JsonProperty("userPhotoUrl")
    private String userPhotoUrl = null;
    
    @JsonProperty("isUserAvailable")
    private Boolean isUserAvailable = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public Boolean getIsUserAvailable() {
        return isUserAvailable;
    }

    public void setIsUserAvailable(Boolean isUserAvailable) {
        this.isUserAvailable = isUserAvailable;
    }
}
