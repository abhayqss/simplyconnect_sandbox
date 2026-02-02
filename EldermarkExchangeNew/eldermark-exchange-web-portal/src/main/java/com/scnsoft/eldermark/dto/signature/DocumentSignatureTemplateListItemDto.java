package com.scnsoft.eldermark.dto.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

public class DocumentSignatureTemplateListItemDto extends IdentifiedNamedTitledEntityDto {
    private boolean isFillable;
    private DocumentSignatureTemplateStatus statusName;
    private String statusTitle;

    public boolean getIsFillable() {
        return isFillable;
    }

    public void setIsFillable(boolean fillable) {
        isFillable = fillable;
    }

    public DocumentSignatureTemplateStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(final DocumentSignatureTemplateStatus statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(final String statusTitle) {
        this.statusTitle = statusTitle;
    }
}
