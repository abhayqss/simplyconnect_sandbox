package com.scnsoft.eldermark.dto.prospect;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProspectOrganizationUniquenessDto {

    private Boolean email;

    public ProspectOrganizationUniquenessDto(Boolean email) {
        this.email = email;
    }

    public Boolean getEmail() {
        return email;
    }
}
