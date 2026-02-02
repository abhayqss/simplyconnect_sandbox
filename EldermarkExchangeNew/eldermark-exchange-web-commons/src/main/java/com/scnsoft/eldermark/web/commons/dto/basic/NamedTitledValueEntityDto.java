package com.scnsoft.eldermark.web.commons.dto.basic;

public class NamedTitledValueEntityDto<T> extends NamedTitledEntityDto implements SingleValueDto<T> {
    private T value;

    public NamedTitledValueEntityDto(String name, String title, T value) {
        super(name, title);
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
