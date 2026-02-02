package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;

import java.util.List;

public interface DocumentFolderPermissionLevelService {
    DocumentFolderPermissionLevel findByCode(DocumentFolderPermissionLevelCode code);
    DocumentFolderPermissionLevel findById(Long id);
    List<DocumentFolderPermissionLevel> findAll();
}
