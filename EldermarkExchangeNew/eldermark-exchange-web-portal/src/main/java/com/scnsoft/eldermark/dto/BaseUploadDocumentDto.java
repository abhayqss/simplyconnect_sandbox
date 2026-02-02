package com.scnsoft.eldermark.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class BaseUploadDocumentDto {

    private String title;
    private MultipartFile document;
    private String description;
    private List<Long> categoryIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MultipartFile getDocument() {
        return document;
    }

    public void setDocument(MultipartFile document) {
        this.document = document;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
