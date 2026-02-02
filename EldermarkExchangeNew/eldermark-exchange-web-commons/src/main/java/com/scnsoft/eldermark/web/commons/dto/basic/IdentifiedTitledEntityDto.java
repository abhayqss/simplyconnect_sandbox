package com.scnsoft.eldermark.web.commons.dto.basic;

public class IdentifiedTitledEntityDto implements IdentifiedEntityDto, TitledEntityDto {
    private Long id;
    private String title;

    public IdentifiedTitledEntityDto() {
    }

    public IdentifiedTitledEntityDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
