package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DocumentSignatureRequestSubmittedFieldSpecificationGenerator {

    public Specification<DocumentSignatureRequestSubmittedField> byRequestIdIn(Collection<Long> requestIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .in(root.get(DocumentSignatureRequestSubmittedField_.SIGNATURE_REQUEST_ID))
                .value(requestIds);
    }

    public Specification<DocumentSignatureRequestSubmittedField> byRequestStatus(DocumentSignatureRequestStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .equal(JpaUtils.getOrCreateJoin(root, DocumentSignatureRequestSubmittedField_.signatureRequest).get(DocumentSignatureRequest_.status), status);
    }
}
