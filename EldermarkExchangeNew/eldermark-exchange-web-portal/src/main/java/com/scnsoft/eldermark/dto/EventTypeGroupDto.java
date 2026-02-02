package com.scnsoft.eldermark.dto;

import java.util.List;

public class EventTypeGroupDto {
    private Long id;
    private String title;
    private String name;
    private Boolean isService;
    private List<EventTypeDto> eventTypes;

    public EventTypeGroupDto(Long id,  String title, String name, Boolean isService,  List<EventTypeDto> eventTypes) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.isService = isService;
        this.eventTypes = eventTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<EventTypeDto> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<EventTypeDto> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setService(Boolean service) {
        isService = service;
    }
}
