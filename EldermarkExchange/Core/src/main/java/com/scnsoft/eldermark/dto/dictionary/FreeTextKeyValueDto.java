package com.scnsoft.eldermark.dto.dictionary;

import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

public class FreeTextKeyValueDto extends KeyValueDto {
    private Boolean isFreeText;

    public Boolean getIsFreeText() {
        return isFreeText;
    }

    public void setIsFreeText(Boolean isFreeText) {
        this.isFreeText = isFreeText;
    }
    public FreeTextKeyValueDto() {
    }

    public FreeTextKeyValueDto(Long id, String name, Boolean isFreeText) {
        super(id,name);
        this.isFreeText = isFreeText;
    }
}
