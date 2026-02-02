package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.EditCommunityDocumentDto;
import com.scnsoft.eldermark.dto.UploadDocumentDto;
import com.scnsoft.eldermark.dto.document.CommunityDocumentItemDto;

import javax.servlet.http.HttpServletResponse;

public interface DocumentFacade {

    Long save(UploadDocumentDto uploadDto);

    boolean canAdd(Long communityId, Long folderId, Long clientId);

    CommunityDocumentItemDto findById(Long documentId);

    void download(Long documentId, HttpServletResponse response, boolean isViewMode);

    void deleteById(Long documentId, boolean isTemporary);

    void restoreById(Long documentId);

    Long edit(EditCommunityDocumentDto editDocumentDto);
}
