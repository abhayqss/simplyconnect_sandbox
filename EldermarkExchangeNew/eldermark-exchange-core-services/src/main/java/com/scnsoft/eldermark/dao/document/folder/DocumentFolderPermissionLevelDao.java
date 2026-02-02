package com.scnsoft.eldermark.dao.document.folder;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevel;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermissionLevelCode;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentFolderPermissionLevelDao extends AppJpaRepository<DocumentFolderPermissionLevel, Long> {
    Optional<DocumentFolderPermissionLevel> findByCode(DocumentFolderPermissionLevelCode code);
}
