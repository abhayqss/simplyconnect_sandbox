package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureTemplateFieldDao extends AppJpaRepository<DocumentSignatureTemplateField, Long> {
}
