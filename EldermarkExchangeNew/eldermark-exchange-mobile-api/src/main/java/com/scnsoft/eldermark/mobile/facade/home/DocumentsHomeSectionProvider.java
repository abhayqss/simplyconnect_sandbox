package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.mobile.dto.home.DocumentHomeSectionDto;

import java.util.Collection;
import java.util.List;

public interface DocumentsHomeSectionProvider {

    List<DocumentHomeSectionDto> loadDocuments(Long currentEmployeeId,
                                               Collection<Long> associatedClientIds,
                                               PermissionFilter permissionFilter, int limit);
}
