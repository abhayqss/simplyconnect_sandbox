package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.DocumentCount;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CustomClientDocumentDao {

    Optional<Instant> findMinDate(Specification<ClientDocument> specification);

    List<DocumentCount> countGroupedBySignatureStatus(Specification<ClientDocument> specification);
}
