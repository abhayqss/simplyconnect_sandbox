package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentTypeDto {
    private Long id;
    private Integer level;
    private String title;
    private Boolean isFreeText;
    private List<IncidentTypeDto> incidentTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsFreeText() {
        return isFreeText;
    }

    public void setIsFreeText(Boolean freeText) {
        isFreeText = freeText;
    }

    public List<IncidentTypeDto> getIncidentTypes() {
        return incidentTypes;
    }

    public void setIncidentTypes(List<IncidentTypeDto> incidents) {
        this.incidentTypes = incidents;
    }
}
