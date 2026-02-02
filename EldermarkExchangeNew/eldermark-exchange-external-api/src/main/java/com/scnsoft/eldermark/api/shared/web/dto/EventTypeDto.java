package com.scnsoft.eldermark.api.shared.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "This DTO is intended to represent an event type.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-03T18:39:19.532+03:00")
public class EventTypeDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("enabled")
    private Boolean enabled = null;

    @JsonProperty("editable")
    private Boolean editable = null;

    @JsonProperty("code")
    private String code = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("groupId")
    private Long groupId = null;

    @JsonProperty("group")
    private EventTypeGroupDto eventGroup = null;

    /**
     * Event type ID
     *
     * @return id
     */
    @ApiModelProperty(value = "Event type ID")
    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Is notification enabled for this event type? This attribute is null if the list of Event Types is requested from /info/eventtypes
     *
     * @return enabled
     */
    @ApiModelProperty(value = "Is notification enabled for this event type? This attribute is null if the list of Event Types is requested from /info/eventtypes")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Is current user able to change this notification setting? This attribute is null if the list of Event Types is requested from /info/eventtypes
     *
     * @return editable
     */
    @ApiModelProperty(value = "Is current user able to change this notification setting? This attribute is null if the list of Event Types is requested from /info/eventtypes")
    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    @ApiModelProperty
    @Size(max = 50)
    @NotNull
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Event type description
     *
     * @return description
     */
    @ApiModelProperty(example = "Accident requiring treatment", value = "Event type description")
    @NotNull
    @Size(max = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Event type group ID
     *
     * @return groupId
     */
    @ApiModelProperty(required = true, value = "Event type group ID (see `GET /info/eventgroups`)")
    @NotNull
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public EventTypeGroupDto getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventTypeGroupDto eventGroup) {
        this.eventGroup = eventGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventTypeDto that = (EventTypeDto) o;

        if (!getId().equals(that.getId())) {
            return false;
        }
        if (!getCode().equals(that.getCode())) {
            return false;
        }
        if (!getDescription().equals(that.getDescription())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getCode().hashCode();
        result = 31 * result + getDescription().hashCode();
        return result;
    }

}
