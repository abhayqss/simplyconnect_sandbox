package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

public class EventTypeDto extends IdentifiedNamedTitledEntityDto {
    private Boolean isService;

    public EventTypeDto() {
    }

    public EventTypeDto(Long id, String name, String title, Boolean isService) {
        super(id, name, title);
        this.isService = isService;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setIsService(Boolean service) {
        isService = service;
    }
}
