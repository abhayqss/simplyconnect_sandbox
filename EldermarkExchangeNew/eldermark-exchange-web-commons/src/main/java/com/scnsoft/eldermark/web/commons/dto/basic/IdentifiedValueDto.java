package com.scnsoft.eldermark.web.commons.dto.basic;

public class IdentifiedValueDto<T> implements IdentifiedEntityDto, SingleValueDto<T> {
    private Long id;
    private T value;

    public IdentifiedValueDto(Long id, T value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
