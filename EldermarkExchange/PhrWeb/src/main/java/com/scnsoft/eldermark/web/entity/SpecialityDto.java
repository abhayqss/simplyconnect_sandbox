package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-29T17:28:56.206+03:00")
public class SpecialityDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("displayName")
    private String displayName = null;


    /**
     * Speciality ID
     *
     * @return id
     */
    @ApiModelProperty(value = "Speciality ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Speciality description
     *
     * @return displayName
     */
    @ApiModelProperty(example = "Primary physician", value = "Speciality description")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SpecialityDto that = (SpecialityDto) o;

        if (!getId().equals(that.getId())) {
            return false;
        }
        if (!getDisplayName().equals(that.getDisplayName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getDisplayName().hashCode();
        return result;
    }
}

