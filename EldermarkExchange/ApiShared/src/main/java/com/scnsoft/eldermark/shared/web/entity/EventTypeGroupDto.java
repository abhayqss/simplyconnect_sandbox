package com.scnsoft.eldermark.shared.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This DTO is intended to represent an info for events grouping.
 */
@ApiModel(description = "This DTO is intended to represent an info for events grouping.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-03T18:39:19.532+03:00")
public class EventTypeGroupDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("priority")
    private Integer priority = null;


    /**
     * Group ID
     * minimum: 1
     *
     * @return id
     */
    @ApiModelProperty(required = true, value = "Group ID")
    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Group name
     *
     * @return name
     */
    @ApiModelProperty(example = "Behavior / Mental Health", required = true, value = "Group name")
    @NotNull
    @Size(max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Group priority. Lower numbers are higher priority.
     * minimum: 1
     *
     * @return priority
     */
    @ApiModelProperty(example = "4", required = true, value = "Group priority. Lower numbers are higher priority.")
    @NotNull
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}

