package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureHistoryCommentsAware;
import com.scnsoft.eldermark.dao.signature.DocumentSignatureHistoryDao;
import com.scnsoft.eldermark.dao.specification.DocumentSignatureHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistoryAction;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.Normalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DocumentSignatureHistoryServiceImpl implements DocumentSignatureHistoryService {

    @Autowired
    private DocumentSignatureHistoryDao historyDao;

    @Autowired
    private DocumentSignatureHistorySpecificationGenerator historySpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSignatureHistory> findByDocumentId(Long documentId, Pageable pageable) {
        return historyDao.findAll(historySpecificationGenerator.byDocumentId(documentId), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getSignatureCountsByDocument(Long documentId) {
        return historyDao.count(
                historySpecificationGenerator.byDocumentId(documentId)
                        .and(historySpecificationGenerator.byAction(DocumentSignatureHistoryAction.DOCUMENT_SIGNED))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public <P> Optional<P> findByDocumentSignedEventId(Long eventId, Class<P> projectionClass) {
        return historyDao.findFirst(
                historySpecificationGenerator.bySignatureRequestCompletedEventId(eventId),
                projectionClass
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCommentsForHistory(DocumentSignatureHistory history, Integer timezoneOffset) {
        return generateCommentsForHistory(DocumentSignatureHistoryCommentsAware.fromEntity(history), timezoneOffset);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCommentsForHistory(DocumentSignatureHistoryCommentsAware history, Integer timezoneOffset) {
        if (history.getAction().isSignatureRequestSentAction()) {

            var subject = history.getAction() == DocumentSignatureHistoryAction.DOCUMENT_SIGNATURE_REQUESTED
                    ? "Signature"
                    : "Document";

            var expirationDate = DateTimeUtils.formatDateTime(history.getRequestDateExpires(), timezoneOffset);

            var comments = subject + " expiration date: " + expirationDate + "\n";
            if (history.getRequestNotificationMethod() != null) {
                comments += "Recipient notification method: " + history.getRequestNotificationMethod().getTitle() + "\n";
                if (history.getRequestNotificationMethod() == SignatureRequestNotificationMethod.SMS) {
                    comments += "Notification sent to: +" + Normalizer.normalizePhone(history.getRequestPhoneNumber());
                } else if (history.getRequestNotificationMethod() == SignatureRequestNotificationMethod.EMAIL) {
                    comments += "Notification sent to: " + history.getRequestEmail();
                }
            }
            return comments;
        } else if (history.getAction() == DocumentSignatureHistoryAction.DOCUMENT_SIGNATURE_REQUEST_EXPIRED) {
            return "Date and time the document expired: " + DateTimeUtils.formatDateTime(history.getDate(), timezoneOffset);
        } else if (history.getAction() == DocumentSignatureHistoryAction.DOCUMENT_SIGNATURE_FAILED) {
            return "Reason of Signature failure: " + history.getRequestPdcflowErrorMessage();
        } else if (history.getAction().isSignatureRequestCompletedAction()) {
            if (history.getRequestNotificationMethod() == SignatureRequestNotificationMethod.SIGN_NOW) {
                return "Sign Now Flow";
            }
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFirstSignature(DocumentSignatureRequest documentSignatureRequest) {
        if (documentSignatureRequest.getDocument().getId() == null) {
            return true;
        }
        return !historyDao.exists(
                historySpecificationGenerator.byDocumentId(documentSignatureRequest.getDocument().getId())
                        .and(historySpecificationGenerator.byAction(DocumentSignatureHistoryAction.DOCUMENT_SIGNED))
        );
    }
}
