package com.scnsoft.eldermark.dao.document.folder;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DocumentFolderDao extends AppJpaRepository<DocumentFolder, Long> {

    boolean existsByCommunityIdAndParentIdAndNameAndIdNotAndDeletionTimeIsNull(Long communityId, Long parentId, String name, Long id);

    boolean existsByCommunityIdAndParentIdAndNameAndDeletionTimeIsNull(Long communityId, Long parentId, String name);

    Collection<DocumentFolder> findByParentId(Long parentId);

    @Modifying
    @Query("update DocumentFolder f set f.isSecurityEnabled = :isSecurityEnabled where f.id in :folderIds")
    void updateSecurityEnabled(@Param("folderIds") List<Long> folders, @Param("isSecurityEnabled") boolean isSecurityEnabled);

    Optional<DocumentFolder> findByCommunityIdAndIdIn(@Param("communityId") Long communityId, @Param("folderIds") Set<Long> folderIds);
}
