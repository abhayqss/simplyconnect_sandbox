package com.scnsoft.eldermark.facade.document.folder;

import com.scnsoft.eldermark.dto.document.folder.*;
import com.scnsoft.eldermark.service.document.folder.PermissionContactFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DocumentFolderFacade {

    DocumentFolderDto findById(Long folderId);

    List<DocumentFolderItemDto> getList(DocumentFolderFilterDto filter);

    Long add(DocumentFolderDto dto);

    Long edit(DocumentFolderDto dto);

    boolean validateUniqueness(Long id, Long parentId, Long communityId, String name);

    List<DocumentFolderPermissionLevelDto> findPermissionLevels();

    DocumentFolderDto getDefaultFolder(Long folderId, Long parentFolderId, Long communityId, String name, boolean isSecurityEnabled);

    Page<PermissionContactDto> getContacts(Long folderId, Long parentFolderId, Long communityId, PermissionContactFilter filter, Pageable pageable);

    boolean canAdd(Long parentFolderId, Long communityId);

    boolean canView(Long folderId, Long communityId);

    void download(Long id, HttpServletResponse httpResponse);

    void delete(Long id, boolean isTemporary);

    void restore(Long id);

    List<DocumentFolderItemDto> getDefaultTemplateFolders(Long organizationId, List<Long> communityIds);
}
