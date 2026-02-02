package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-02T10:35:33.724+03:00")
public class NotificationSettingsDto {

    @JsonProperty("notificationChannels")
    private Map<NotificationType, Boolean> notificationChannels = null;

    @JsonProperty("eventTypes")
    private List<EventTypeDto> eventTypes = new ArrayList<EventTypeDto>();


    @ApiModelProperty
    public Map<NotificationType, Boolean> getNotificationChannels() {
        return notificationChannels;
    }

    public void setNotificationChannels(Map<NotificationType, Boolean> notificationChannels) {
        this.notificationChannels = notificationChannels;
    }

    public NotificationSettingsDto addEventTypesItem(EventTypeDto eventTypesItem) {
        this.eventTypes.add(eventTypesItem);
        return this;
    }

    @ApiModelProperty
    public List<EventTypeDto> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventTypeDto> eventTypes) {
        this.eventTypes = eventTypes;
    }

}
