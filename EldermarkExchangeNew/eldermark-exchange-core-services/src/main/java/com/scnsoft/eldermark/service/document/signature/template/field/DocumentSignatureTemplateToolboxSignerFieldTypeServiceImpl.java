package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureTemplateToolboxSignerFieldTypeDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateToolboxSignerFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureTemplateToolboxSignerFieldTypeServiceImpl implements DocumentSignatureTemplateToolboxSignerFieldTypeService {

    @Autowired
    private DocumentSignatureTemplateToolboxSignerFieldTypeDao toolboxSignerFieldTypeDao;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureTemplateToolboxSignerFieldType> findAll() {
        return toolboxSignerFieldTypeDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DocumentSignatureTemplateToolboxSignerFieldType> getTypesMapById() {
        return toolboxSignerFieldTypeDao.findAll().stream()
            .collect(Collectors.toMap(DocumentSignatureTemplateToolboxSignerFieldType::getId, Function.identity()));
    }
}
