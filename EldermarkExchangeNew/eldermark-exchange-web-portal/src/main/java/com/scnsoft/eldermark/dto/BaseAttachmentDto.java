package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.BaseAttachment_;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseAttachmentDto {

    private long id;
    @EntitySort(BaseAttachment_.FILE_NAME)
    private String name;
    private String mimeType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
