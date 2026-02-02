package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureHistoryCommentsAware;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DocumentSignatureHistoryService {

    Page<DocumentSignatureHistory> findByDocumentId(Long documentId, Pageable pageable);

    long getSignatureCountsByDocument(Long documentId);

    <P> Optional<P> findByDocumentSignedEventId(Long eventId, Class<P> projectionClass);

    String generateCommentsForHistory(DocumentSignatureHistory history, Integer timezoneOffset);

    String generateCommentsForHistory(DocumentSignatureHistoryCommentsAware history, Integer timezoneOffset);

    boolean isFirstSignature(DocumentSignatureRequest documentSignatureRequest);
}
