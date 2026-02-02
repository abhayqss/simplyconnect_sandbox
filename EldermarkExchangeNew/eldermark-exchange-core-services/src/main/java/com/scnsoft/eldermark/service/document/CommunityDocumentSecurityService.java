package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;

public interface CommunityDocumentSecurityService {

    boolean canViewList(CommunityDocumentSecurityFieldsAware dto);

    boolean canUpload(CommunityDocumentSecurityFieldsAware dto);

    boolean canView(long documentId);

    boolean canDownload(long documentId);

    boolean canDownload(CommunityDocumentSecurityFieldsAware dto);

    boolean canDelete(long documentId);

    boolean canDelete(CommunityDocumentSecurityFieldsAware dto);

    boolean canEdit(long documentId);

    boolean canEdit(CommunityDocumentSecurityFieldsAware dto);
}
