package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentCommentedNotificationDto extends IncidentNotificationDto {

    @NotEmpty
    @Override
    public String getComment() {
        return super.getComment();
    }
}
