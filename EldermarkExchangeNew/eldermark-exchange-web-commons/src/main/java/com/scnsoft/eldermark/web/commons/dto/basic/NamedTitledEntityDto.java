package com.scnsoft.eldermark.web.commons.dto.basic;

public class NamedTitledEntityDto implements NamedEntityDto, TitledEntityDto {
    String name;
    String title;

    public NamedTitledEntityDto() {
    }

    public NamedTitledEntityDto(String name, String title) {
        this.name = name;
        this.title = title;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
