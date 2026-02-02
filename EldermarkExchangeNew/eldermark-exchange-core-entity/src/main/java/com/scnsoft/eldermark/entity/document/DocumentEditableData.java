package com.scnsoft.eldermark.entity.document;

import java.util.List;
import java.util.Objects;

public class DocumentEditableData {
    private Long id;
    private String title;
    private String description;
    private List<Long> categoryIds;

    public DocumentEditableData(Long id, String title, String description, List<Long> categoryIds) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.categoryIds = categoryIds;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }
}
