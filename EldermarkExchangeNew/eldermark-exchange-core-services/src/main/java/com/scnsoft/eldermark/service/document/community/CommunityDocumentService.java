package com.scnsoft.eldermark.service.document.community;

import com.scnsoft.eldermark.beans.security.projection.dto.CommunityDocumentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.document.CommunityDocumentEditableData;
import com.scnsoft.eldermark.entity.document.community.CommunityDocument;
import com.scnsoft.eldermark.service.SecurityAwareEntityService;
import com.scnsoft.eldermark.service.document.DocumentService;
import org.springframework.transaction.annotation.Transactional;

public interface CommunityDocumentService
    extends SecurityAwareEntityService<CommunityDocumentSecurityFieldsAware, Long>, DocumentService {

    CommunityDocument findById(long id);

    Long edit(CommunityDocumentEditableData documentEditableData);
}
