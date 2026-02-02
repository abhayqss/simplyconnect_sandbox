package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.DocumentCategorySecurityFieldsAware;

public interface DocumentCategorySecurityService {

    boolean canAdd(DocumentCategorySecurityFieldsAware dto);

    boolean canViewList(Long organizationId);

    boolean canEdit(Long id);

    boolean canDelete(Long id);
}
