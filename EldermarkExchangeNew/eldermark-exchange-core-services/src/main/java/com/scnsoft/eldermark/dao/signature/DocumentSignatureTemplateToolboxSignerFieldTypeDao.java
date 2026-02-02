package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateToolboxSignerFieldType;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureTemplateToolboxSignerFieldTypeDao
    extends AppJpaRepository<DocumentSignatureTemplateToolboxSignerFieldType, Long> {
}
