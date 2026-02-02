package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolderParentAware;

public interface DocumentFolderParentWithSecurityAware extends DocumentFolderParentAware {
    Boolean getIsSecurityEnabled();
}
