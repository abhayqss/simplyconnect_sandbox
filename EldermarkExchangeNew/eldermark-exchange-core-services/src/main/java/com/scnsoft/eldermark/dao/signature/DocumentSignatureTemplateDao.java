package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentSignatureTemplateDao extends AppJpaRepository<DocumentSignatureTemplate, Long> {

}