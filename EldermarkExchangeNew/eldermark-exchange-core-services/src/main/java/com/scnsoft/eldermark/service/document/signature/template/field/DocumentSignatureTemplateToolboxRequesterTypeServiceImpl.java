package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureTemplateToolboxRequesterFieldTypeDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateToolboxRequesterFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureTemplateToolboxRequesterTypeServiceImpl implements DocumentSignatureTemplateToolboxRequesterTypeService {

    @Autowired
    private DocumentSignatureTemplateToolboxRequesterFieldTypeDao toolboxRequesterFieldTypeDao;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DocumentSignatureTemplateToolboxRequesterFieldType> getTypesMapById() {
        return toolboxRequesterFieldTypeDao.findAll().stream()
                .collect(Collectors.toMap(DocumentSignatureTemplateToolboxRequesterFieldType::getId, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureTemplateToolboxRequesterFieldType> findAll() {
        return toolboxRequesterFieldTypeDao.findAll();
    }
}
