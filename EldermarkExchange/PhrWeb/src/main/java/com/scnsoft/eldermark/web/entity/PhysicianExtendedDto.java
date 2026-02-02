package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * Extended physician object.
 */
@ApiModel(description = "Extended physician object.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-15T16:05:55.768+03:00")
public class PhysicianExtendedDto {

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

    @JsonProperty("professionalInfo")
    private ProfessionalProfileDto professionalInfo = null;


    @ApiModelProperty
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * user id
     * minimum: 1
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

    /**
     * A map of professional data (field name / field value approach).
     *
     * @return professionalInfo
     */
    @ApiModelProperty(value = "A map of professional data (field name / field value approach).")
    public ProfessionalProfileDto getProfessionalInfo() {
        return professionalInfo;
    }

    public void setProfessionalInfo(ProfessionalProfileDto professionalInfo) {
        this.professionalInfo = professionalInfo;
    }

}

