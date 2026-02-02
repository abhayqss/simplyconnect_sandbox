package com.scnsoft.eldermark.dao.document.folder;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFolderPermissionDao extends AppJpaRepository<DocumentFolderPermission, Long> {
    void deleteByFolder_IdInAndEmployeeIdNotIn(List<Long> folderIds, List<Long> employeeIds);
}
