package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-10T12:36:07.110+03:00")
public class EventActivityDto extends ActivityDto {

    @JsonProperty("eventType")
    private String eventType = null;

    @JsonProperty("eventId")
    private Long eventId = null;

    @JsonProperty("unread")
    private Boolean unread = null;

    @JsonProperty("responsibility")
    private String responsibility = null;


    /**
     * Event type description
     *
     * @return eventType
     */
    @ApiModelProperty(value = "Event type description")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Event ID
     *
     * @return eventId
     */
    @ApiModelProperty(value = "Event ID")
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * item status. true if current user hasn't seen this item yet.
     *
     * @return unread
     */
    @ApiModelProperty(value = "item status. true if current user hasn't seen this item yet.")
    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }

    @ApiModelProperty
    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

}

