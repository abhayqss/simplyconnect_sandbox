package com.scnsoft.eldermark.mobile.dto.document;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.document.ClientDocument_;
import com.scnsoft.eldermark.utils.CustomSortUtils;
import org.springframework.data.domain.Sort;

import java.util.List;

public class BaseDocumentDto {
    private Long id;

    @EntitySort(ClientDocument_.DOCUMENT_TITLE)
    private String title;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(value = CustomSortUtils.EXPRESSION_ORDER_PREFIX + CustomSortUtils.Functions.FIRST_NON_NULL + "("
            + ClientDocument_.UPDATE_TIME + ", "
            + ClientDocument_.CREATION_TIME +")")
    private Long createdOrModifiedDate;

    private String mimeType;

    private List<DocumentCategoryItemDto> categories;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreatedOrModifiedDate() {
        return createdOrModifiedDate;
    }

    public void setCreatedOrModifiedDate(Long createdOrModifiedDate) {
        this.createdOrModifiedDate = createdOrModifiedDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<DocumentCategoryItemDto> getCategories() {
        return categories;
    }

    public void setCategories(List<DocumentCategoryItemDto> categories) {
        this.categories = categories;
    }
}
