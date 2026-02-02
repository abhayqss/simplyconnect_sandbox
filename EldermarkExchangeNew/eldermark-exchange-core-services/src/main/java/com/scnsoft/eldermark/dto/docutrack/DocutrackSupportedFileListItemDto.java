package com.scnsoft.eldermark.dto.docutrack;

public class DocutrackSupportedFileListItemDto {
    private String mimeType;
    private Long maxSize;
    private Boolean isConversionNeeded;

    public DocutrackSupportedFileListItemDto() {
    }

    public DocutrackSupportedFileListItemDto(String mimeType, Long maxSize, Boolean isConversionNeeded) {
        this.mimeType = mimeType;
        this.maxSize = maxSize;
        this.isConversionNeeded = isConversionNeeded;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    public Boolean getIsConversionNeeded() {
        return isConversionNeeded;
    }

    public void setIsConversionNeeded(Boolean isConversionNeeded) {
        this.isConversionNeeded = isConversionNeeded;
    }
}
