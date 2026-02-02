package com.scnsoft.eldermark.web.commons.dto.basic;

public class IdentifiedNamedTitledEntityDto extends IdentifiedNamedEntityDto implements TitledEntityDto {
    private String title;

    public IdentifiedNamedTitledEntityDto() {
    }

    public IdentifiedNamedTitledEntityDto(Long id, String name, String title) {
        super(id, name);
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
