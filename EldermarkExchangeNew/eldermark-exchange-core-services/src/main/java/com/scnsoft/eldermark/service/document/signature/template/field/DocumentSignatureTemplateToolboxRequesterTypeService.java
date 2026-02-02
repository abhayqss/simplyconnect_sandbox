package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateToolboxRequesterFieldType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface DocumentSignatureTemplateToolboxRequesterTypeService {

    @Transactional(readOnly = true)
    Map<Long, DocumentSignatureTemplateToolboxRequesterFieldType> getTypesMapById();

    List<DocumentSignatureTemplateToolboxRequesterFieldType> findAll();
}
