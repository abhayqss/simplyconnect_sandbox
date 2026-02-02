package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentRespondedNotificationDto extends IncidentNotificationDto {

    @NotEmpty
    @Override
    public String getResponse() {
        return super.getResponse();
    }

    @NotNull
    @Override
    public Long getResponseDate() {
        return super.getResponseDate();
    }
}
