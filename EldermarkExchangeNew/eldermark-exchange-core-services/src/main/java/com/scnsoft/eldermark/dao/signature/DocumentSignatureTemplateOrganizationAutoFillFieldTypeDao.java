package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateOrganizationAutoFillFieldType;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureTemplateOrganizationAutoFillFieldTypeDao
    extends AppJpaRepository<DocumentSignatureTemplateOrganizationAutoFillFieldType, Long> {
}
