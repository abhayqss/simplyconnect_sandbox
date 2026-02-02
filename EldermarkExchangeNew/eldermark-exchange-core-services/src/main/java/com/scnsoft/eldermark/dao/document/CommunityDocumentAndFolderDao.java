package com.scnsoft.eldermark.dao.document;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.CommunityDocumentAndFolder;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityDocumentAndFolderDao extends AppJpaRepository<CommunityDocumentAndFolder, String> {

    Optional<CommunityDocumentAndFolder> findFirstByCommunityIdAndLastModifiedTimeIsNotNullOrderByLastModifiedTimeAsc(Long communityId);

    <P> P findByIdAndType(String folderId, DocumentAndFolderType type, Class<P> projectionClass);
}
