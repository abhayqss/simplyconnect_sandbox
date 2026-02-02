package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.validation.Uuid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;

/**
 * This DTO is intended to represent Nucleus communication device.
 */
@ApiModel(description = "This DTO is intended to represent Nucleus communication device.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class NucleusDeviceDto {

    @NotNull
    @Uuid
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("location")
    private String location = null;

    @JsonProperty("type")
    private String type = null;


    /**
     * nucleus device id
     *
     * @return id
     */
    @ApiModelProperty(example = "1a8a0c8d-7e16-41be-a73d-c6cc5ed68f07", value = "nucleus device id (36-char UUID)")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * nucleus device location
     *
     * @return location
     */
    @ApiModelProperty(example = "Kitchen", value = "nucleus device location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * device type, such as intercom, mobile app, etc.
     *
     * @return type
     */
    @ApiModelProperty(value = "device type, such as intercom, mobile app, etc.")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NucleusDeviceDto that = (NucleusDeviceDto) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getLocation(), that.getLocation())
                .append(getType(), that.getType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .append(getLocation())
                .append(getType())
                .toHashCode();
    }

}
