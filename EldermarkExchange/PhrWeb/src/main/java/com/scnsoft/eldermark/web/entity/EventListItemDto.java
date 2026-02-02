package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent a brief info about events.
 */
@ApiModel(description = "This DTO is intended to represent a brief info about events.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-06T12:48:37.941+03:00")
public class EventListItemDto {

    @JsonProperty("eventDate")
    private Long eventDate = null;

    @JsonProperty("eventId")
    private Long eventId = null;

    @JsonProperty("eventType")
    private String eventType = null;

    @JsonProperty("eventGroupId")
    private Long eventGroupId = null;

    @JsonProperty("residentName")
    private String residentName = null;

    @JsonProperty("unread")
    private Boolean unread = null;


    /**
     * Date of event
     *
     * @return eventDate
     */
    @ApiModelProperty(example = "1495028820000", value = "Date of event")
    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * event id
     * minimum: 1
     *
     * @return eventId
     */
    @ApiModelProperty(value = "event id")
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @ApiModelProperty(example = "Accident requiring treatment")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * event group id (see `GET /info/eventgroups`)
     * minimum: 1
     *
     * @return eventGroupId
     */
    @ApiModelProperty(value = "event group id (see `GET /info/eventgroups`)")
    public Long getEventGroupId() {
        return eventGroupId;
    }

    public void setEventGroupId(Long eventGroupId) {
        this.eventGroupId = eventGroupId;
    }

    /**
     * Patient's full name
     *
     * @return residentName
     */
    @ApiModelProperty(example = "Charles Xavier", value = "Patient's full name")
    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    /**
     * item status. true if current user hasn't seen this item yet.
     *
     * @return unread
     */
    @ApiModelProperty(value = "list item status. true if current user hasn't seen this item yet.")
    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }

}

