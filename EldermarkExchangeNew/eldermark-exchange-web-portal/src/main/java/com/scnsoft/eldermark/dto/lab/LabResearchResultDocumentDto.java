package com.scnsoft.eldermark.dto.lab;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

public class LabResearchResultDocumentDto extends IdentifiedTitledEntityDto {
    private String mimeType;

    public LabResearchResultDocumentDto(Long id, String title, String mimeType) {
        super(id, title);
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
