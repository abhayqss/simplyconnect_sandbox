package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentPersonalNotificationDto extends IncidentNotificationDto {

    @NotEmpty
    @Override
    public String getFullName() {
        return super.getFullName();
    }

    @NotEmpty
    @Override
    public String getPhone() {
        return super.getPhone();
    }
}
