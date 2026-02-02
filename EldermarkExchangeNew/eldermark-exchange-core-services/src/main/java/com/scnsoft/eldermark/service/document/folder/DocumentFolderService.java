package com.scnsoft.eldermark.service.document.folder;

import com.scnsoft.eldermark.beans.DocumentFolderFilter;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.service.ProjectingService;
import com.scnsoft.eldermark.service.document.DocumentTreeItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.function.Predicate;

public interface DocumentFolderService extends ProjectingService<Long> {

    DocumentFolder findById(Long id);

    List<DocumentFolder> find(DocumentFolderFilter filter);

    Long save(DocumentFolder folder);

    List<FolderPermission> resolveFolderPermissions(DocumentFolder folder);

    List<FolderPermission> resolveFolderPermissions(Long folderId);

    void validateParentCommunity(Long communityId, Long parentId);

    void validateParent(Long folderId, Long parentId);

    void validateUniqueness(Long folderId, Long communityId, Long parentId, String name);

    void validatePermissions(Long communityId, Long parentId, Boolean isSecurityEnabled, List<FolderPermission> permissions);

    boolean isUnique(Long folderId, Long communityId, Long parentId, String name);

    DocumentFolder getDefaultFolder(Employee creator, Long folderId, Long parentFolderId, Long communityId, String name, boolean isSecurityEnabled);

    Page<Employee> getEmployeesAvailableForFolderPermissions(Long parentFolderId, Long communityId, PermissionContactFilter filter, Pageable pageable);

    void temporaryDelete(Long folderId, Employee deletedByEmployee);

    void permanentlyDelete(Long folderId, Employee deletedByEmployee);

    void restore(Long folderId, Employee restoredByEmployee);

    Map<Long, List<DocumentFolder>> getChildrenMap(Long communityId);

    Map<Long, List<DocumentFolder>> getChildrenMap(Long communityId, List<DocumentFolderType> types);

    DocumentTreeItem<Long> getFolderIdTree(Long communityId, Long rootFolderId, Predicate<DocumentFolder> filter);

    Optional<DocumentFolder> findByCommunityIdAndIdIn(Long communityId, Set<Long> folderIds);

    Long createTemplateFolder(Long communityId);

    List<DocumentFolder> findDefaultTemplateFolders(List<Long> communityIds);

    List<DocumentFolder> findByIdIn(Collection<Long> folderIds);

    List<DocumentFolder> findByDocumentSignatureTemplateId(Long documentSignatureTemplateId);
}
