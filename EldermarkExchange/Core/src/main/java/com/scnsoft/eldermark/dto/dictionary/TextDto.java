package com.scnsoft.eldermark.dto.dictionary;

public class TextDto {
    private Long id;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String freeText) {
        this.text = freeText;
    }
}
