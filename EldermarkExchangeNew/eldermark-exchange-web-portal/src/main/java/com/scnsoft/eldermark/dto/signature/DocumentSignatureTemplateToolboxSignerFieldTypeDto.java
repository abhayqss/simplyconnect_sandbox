package com.scnsoft.eldermark.dto.signature;

public class DocumentSignatureTemplateToolboxSignerFieldTypeDto {
    private Long id;
    private String name;
    private String title;
    private Short width;
    private Short height;

    public DocumentSignatureTemplateToolboxSignerFieldTypeDto(
            Long id, String name, String title, Short width, Short height
    ) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Short getWidth() {
        return width;
    }

    public void setWidth(final Short width) {
        this.width = width;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(final Short height) {
        this.height = height;
    }
}
