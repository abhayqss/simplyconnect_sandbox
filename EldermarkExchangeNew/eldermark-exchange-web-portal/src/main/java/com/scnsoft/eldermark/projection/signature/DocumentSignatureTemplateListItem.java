package com.scnsoft.eldermark.projection.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;

public interface DocumentSignatureTemplateListItem extends IdAware, NameAware {
    String getTitle();
    String getFormSchema();
    DocumentSignatureTemplateStatus getStatus();
}
