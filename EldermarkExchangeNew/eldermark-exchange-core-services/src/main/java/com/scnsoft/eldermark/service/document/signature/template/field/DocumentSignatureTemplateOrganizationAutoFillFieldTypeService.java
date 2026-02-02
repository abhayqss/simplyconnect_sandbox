package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateOrganizationAutoFillFieldType;

import java.util.List;
import java.util.Map;

public interface DocumentSignatureTemplateOrganizationAutoFillFieldTypeService {

    Map<Long, DocumentSignatureTemplateOrganizationAutoFillFieldType> getTypesMapById();

    List<DocumentSignatureTemplateOrganizationAutoFillFieldType> findAll();
}
