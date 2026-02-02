package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientDocumentSecurityAwareEntity;

import java.util.Collection;

public interface ClientDocumentSecurityService {

    boolean canViewList();

    boolean canView(long documentId);

    boolean canDownload(long documentId);

    boolean canDownloadAll(Collection<Long> documentIds);

    boolean canDownloadCcd(long clientId);

    boolean canDownloadFacesheet(long clientId);

    boolean canUpload(ClientDocumentSecurityFieldsAware dto);

    boolean canDelete(long documentId);

    boolean canDelete(ClientDocumentSecurityAwareEntity document);

    boolean canDownloadServicePlanPdf(long clientId);

    boolean canEdit(long documentId);

    boolean canEdit(ClientDocumentSecurityAwareEntity document);
}
