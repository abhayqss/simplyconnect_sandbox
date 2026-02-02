package com.scnsoft.eldermark.entity.document.folder;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface DocumentFolderParentAware extends IdAware {
    Long getParentId();
}
