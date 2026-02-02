package com.scnsoft.eldermark.web.commons.dto.basic;

import com.scnsoft.eldermark.beans.projection.IdNameAware;

public class IdentifiedNamedEntityDto implements IdentifiedEntityDto, NamedEntityDto {
    private Long id;
    private String name;

    public IdentifiedNamedEntityDto() {
    }

    public IdentifiedNamedEntityDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdentifiedNamedEntityDto(IdNameAware idNameAware) {
        this(idNameAware.getId(), idNameAware.getName());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
