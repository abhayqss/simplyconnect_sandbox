package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.NoteSecurityFieldsAware;

public interface NoteSecurityService {

    boolean canAdd(NoteSecurityFieldsAware dto);

    boolean canAddGroupNoteToCommunity(Long communityId);

    boolean canEdit(Long noteId);

    boolean canViewList();

    boolean canView(Long noteId);

}
