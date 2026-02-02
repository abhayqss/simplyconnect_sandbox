package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureFieldPdcFlowTypeAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.signature.*;

import java.util.List;

public interface DocumentSignatureFieldData extends DocumentSignatureFieldPdcFlowTypeAware, IdAware {

    String getName();

    ScSourceTemplateFieldType getScSourceFieldType();

    BaseDocumentSignatureFieldLocation getLocation();

    Long getRelatedFieldId();

    String getRelatedFieldValue();

    List<BaseDocumentSignatureFieldStyle> getStyles();
}
