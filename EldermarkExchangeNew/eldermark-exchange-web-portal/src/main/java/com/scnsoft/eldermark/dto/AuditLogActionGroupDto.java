package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;

import java.util.List;

public class AuditLogActionGroupDto extends NamedTitledEntityDto {

    private List<IdentifiedNamedTitledEntityDto> activities;

    public AuditLogActionGroupDto(String name, String title, List<IdentifiedNamedTitledEntityDto> activities) {
        super(name, title);
        this.activities = activities;
    }

    public List<IdentifiedNamedTitledEntityDto> getActivities() {
        return activities;
    }

    public void setActivities(List<IdentifiedNamedTitledEntityDto> activities) {
        this.activities = activities;
    }
}
