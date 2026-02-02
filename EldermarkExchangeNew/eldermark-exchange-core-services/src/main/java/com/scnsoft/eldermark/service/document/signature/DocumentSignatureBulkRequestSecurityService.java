package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.TemplateSignatureBulkRequestSecurityFieldsAware;

public interface DocumentSignatureBulkRequestSecurityService {
    boolean canSubmit(TemplateSignatureBulkRequestSecurityFieldsAware bulkRequestAware);

    boolean canRenew(Long bulkRequestId);

    boolean canCancel(Long bulkRequestId);

    boolean canView(Long bulkRequestId);
}