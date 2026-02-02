package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateAutoFillFieldType;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureTemplateAutoFillFieldTypeDao extends AppJpaRepository<DocumentSignatureTemplateAutoFillFieldType, Long> {
}
