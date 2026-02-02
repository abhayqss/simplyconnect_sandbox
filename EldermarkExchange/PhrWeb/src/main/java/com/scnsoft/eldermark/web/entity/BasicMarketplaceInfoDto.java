package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class BasicMarketplaceInfoDto {
    @JsonProperty("id")
    private Long id = null;
    @JsonProperty("communityName")
    private String communityName = null;
    @JsonProperty("organizationName")
    private String organizationName = null;
    @JsonProperty("communityTypes")
    private List<String> communityTypes = new ArrayList<String>();
    @JsonProperty("address")
    private String address = null;
    @JsonProperty("location")
    private LocationDto location = null;

    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Community Name. It contains a single value.
     * @return communityName
     */
    @ApiModelProperty(example = "Florida Assisted Living", value = "Community Name. It contains a single value.")
    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    /**
     * Organization name. It contains a single value.
     * @return organizationName
     */
    @ApiModelProperty(example = "RBA", value = "Organization name. It contains a single value.")
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public BasicMarketplaceInfoDto addCommunityTypesItem(String communityTypesItem) {
        this.communityTypes.add(communityTypesItem);
        return this;
    }

    /**
     * Community Types. It contains a list of values.
     * @return communityTypes
     */
    @ApiModelProperty(value = "Community Types. It contains a list of values.")
    public List<String> getCommunityTypes() {
        return communityTypes;
    }

    public void setCommunityTypes(List<String> communityTypes) {
        this.communityTypes = communityTypes;
    }

    /**
     * Community address. It contains a single value.
     * @return address
     */
    @ApiModelProperty(example = "3290 Hermosillo Place Washington, DC 20521", value = "Community address. It contains a single value.")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Location of object.
     * @return location
     */
    @ApiModelProperty(value = "Location of object.")
    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }
}
