package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDocumentDao extends AppJpaRepository<ClientDocument, Long>, CustomClientDocumentDao {
}
