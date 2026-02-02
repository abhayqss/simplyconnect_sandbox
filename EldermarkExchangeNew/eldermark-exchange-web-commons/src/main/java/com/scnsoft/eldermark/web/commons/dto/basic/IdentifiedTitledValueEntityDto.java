package com.scnsoft.eldermark.web.commons.dto.basic;

public class IdentifiedTitledValueEntityDto<T> implements NamedEntityDto, SingleValueDto<T>, IdentifiedEntityDto {
    private Long id;
    private String name;
    private T value;

    public IdentifiedTitledValueEntityDto(Long id, String name, T value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }
}
