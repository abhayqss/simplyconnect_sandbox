package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;

import java.util.List;

public interface DocumentSignatureTemplateSecurityService {

    boolean canViewList(Long communityId);

    boolean canViewList(List<Long> communityIds);

    boolean canView(Long id);

    boolean canView(Long id, Long communityId);

    boolean canAdd(Long organizationId);

    boolean canEdit(Long id);

    boolean canEdit(DocumentSignatureTemplate template);

    boolean canDelete(Long id);

    boolean canDelete(DocumentSignatureTemplate template);

    boolean canAssign(Long id);

    boolean canCopy(Long id);
}
