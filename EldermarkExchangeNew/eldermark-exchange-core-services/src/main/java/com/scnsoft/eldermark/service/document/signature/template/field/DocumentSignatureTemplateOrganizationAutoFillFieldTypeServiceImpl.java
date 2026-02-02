package com.scnsoft.eldermark.service.document.signature.template.field;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureTemplateOrganizationAutoFillFieldTypeDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateOrganizationAutoFillFieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureTemplateOrganizationAutoFillFieldTypeServiceImpl implements DocumentSignatureTemplateOrganizationAutoFillFieldTypeService {

    @Autowired
    private DocumentSignatureTemplateOrganizationAutoFillFieldTypeDao typeDao;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DocumentSignatureTemplateOrganizationAutoFillFieldType> getTypesMapById() {
        return typeDao.findAll().stream()
                .collect(Collectors.toMap(DocumentSignatureTemplateOrganizationAutoFillFieldType::getId, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureTemplateOrganizationAutoFillFieldType> findAll() {
        return typeDao.findAll();
    }
}
