package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureRequestFromAware;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;

import java.util.List;

public interface DocumentSignatureRequestSecurityService {

    long ANY_TEMPLATE = -1;

    boolean canAdd(DocumentSignatureRequestSecurityFieldsAware dto);

    boolean canAddAll(List<DocumentSignatureRequestSecurityFieldsAware> dtos);

    boolean canSign(Long requestId);

    boolean canSign(DocumentSignatureRequestFromAware request);

    boolean canCancel(Long requestId);

    boolean canResendPin(Long requestId);

    boolean canRenew(Long requestId);

    boolean canAddForOrganization(Long organizationId);
}
