package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

public class RoleDto extends IdentifiedNamedTitledEntityDto {
    public RoleDto(Long id, String name, String title) {
        super(id, name, title);
    }
}
