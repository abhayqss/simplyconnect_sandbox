package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureRequestSubmittedFieldDao extends AppJpaRepository<DocumentSignatureRequestSubmittedField, Long> {
}
