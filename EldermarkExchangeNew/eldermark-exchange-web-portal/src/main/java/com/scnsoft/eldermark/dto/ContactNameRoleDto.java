package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;

public class ContactNameRoleDto extends IdentifiedNamedEntityDto {

    private String role;

    public ContactNameRoleDto(Long id, String name, String role) {
        super(id, name);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
