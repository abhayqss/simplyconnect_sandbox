package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * Physician object (extended info)
 */
@ApiModel(description = "Physician object (extended info)")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-15T10:23:24.023+03:00")
public class PhysicianExtendedDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("fullName")
    private String fullName = null;

    @JsonProperty("speciality")
    private String speciality = null;

    @JsonProperty("professionalInfo")
    private ProfessionalProfileDto professionalInfo = null;


    /**
     * physician id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "physician id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
