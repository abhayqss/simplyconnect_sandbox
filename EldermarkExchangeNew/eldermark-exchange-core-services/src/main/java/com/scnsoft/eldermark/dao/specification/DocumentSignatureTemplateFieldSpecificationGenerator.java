package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DocumentSignatureTemplateFieldSpecificationGenerator {

    public Specification<DocumentSignatureTemplateField> isDefaultValueTypeNotNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(DocumentSignatureTemplateField_.defaultValueType));
    }

    public Specification<DocumentSignatureTemplateField> isScSourceFieldTypeNotNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(DocumentSignatureTemplateField_.scSourceFieldType));
    }

    public Specification<DocumentSignatureTemplateField> byTemplateId(Long templateId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(DocumentSignatureTemplateField_.signatureTemplate).get(DocumentSignatureTemplate_.id), templateId);
    }
}
