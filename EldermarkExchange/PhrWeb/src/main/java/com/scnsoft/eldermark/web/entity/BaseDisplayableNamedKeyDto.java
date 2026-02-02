package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class BaseDisplayableNamedKeyDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("key")
    private String key = null;

    public BaseDisplayableNamedKeyDto(Long id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }

    public BaseDisplayableNamedKeyDto() {
    }

    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Care Type Name. It contains a single value.
     * @return name
     */
    @ApiModelProperty(example = "Primary Care", value = "Care Type Name. It contains a single value.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Care Type key. It contains a single value.
     * @return key
     */
    @ApiModelProperty(example = "PRIMARY_CARE", value = "Care Type key. It contains a single value.")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
