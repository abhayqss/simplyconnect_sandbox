package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientOrganizationUniquenessDto {

    private Boolean email;

    public ClientOrganizationUniquenessDto(Boolean email) {
        this.email = email;
    }

    public Boolean getEmail() {
        return email;
    }
}
