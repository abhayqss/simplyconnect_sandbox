package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * Physician object (brief info)
 */
@ApiModel(description = "Physician object (brief info)")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-30T18:42:18.084+03:00")
public class PhysicianDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("photoUrl")
    private String photoUrl = null;

    @JsonProperty("fullName")
    private String fullName = null;

    @JsonProperty("speciality")
    private String speciality = null;


    /**
     * physician id
     * minimum: 1
     *
     * @return id
     */
    @ApiModelProperty(value = "physician id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * user id
     *
     * @return userId
     */
    @ApiModelProperty(value = "user id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * url. Photo url can be constructed like /phr/{userId}/profile/avatar
     *
     * @return photoUrl
     */
    @ApiModelProperty(value = "url. Photo url can be constructed like /phr/{userId}/profile/avatar")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @ApiModelProperty(example = "Roy Stevens")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @ApiModelProperty(example = "Behavioral health")
    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PhysicianDto that = (PhysicianDto) o;

        if (!getId().equals(that.getId())) {
            return false;
        }
        if (!getUserId().equals(that.getUserId())) {
            return false;
        }
        if (!getPhotoUrl().equals(that.getPhotoUrl())) {
            return false;
        }
        if (!getFullName().equals(that.getFullName())) {
            return false;
        }
        if (getSpeciality() != null ? !getSpeciality().equals(that.getSpeciality()) : that.getSpeciality() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getUserId().hashCode();
        result = 31 * result + getPhotoUrl().hashCode();
        result = 31 * result + getFullName().hashCode();
        result = 31 * result + (getSpeciality() != null ? getSpeciality().hashCode() : 0);
        return result;
    }

}

