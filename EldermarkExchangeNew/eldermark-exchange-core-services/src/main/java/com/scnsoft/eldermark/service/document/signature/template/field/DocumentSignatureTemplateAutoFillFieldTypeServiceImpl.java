package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureTemplateAutoFillFieldTypeDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateAutoFillFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureTemplateAutoFillFieldTypeServiceImpl implements DocumentSignatureTemplateAutoFillFieldTypeService {

    @Autowired
    private DocumentSignatureTemplateAutoFillFieldTypeDao typeDao;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DocumentSignatureTemplateAutoFillFieldType> getTypesMapById() {
        return typeDao.findAll().stream()
                .collect(Collectors.toMap(DocumentSignatureTemplateAutoFillFieldType::getId, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureTemplateAutoFillFieldType> findAll() {
        return typeDao.findAll();
    }
}
