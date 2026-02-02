package com.scnsoft.eldermark.facade.document;

import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.dto.document.DocumentAndFolderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CommunityDocumentAndFolderFacade {

    Page<DocumentAndFolderItemDto> find(CommunityDocumentFilterDto documentFilter, Pageable pageable);

    Long count(CommunityDocumentFilterDto documentFilter);

    void download(List<String> ids, HttpServletResponse httpResponse);

    Long getOldestDate(Long communityId);

    Boolean canViewList();
}
