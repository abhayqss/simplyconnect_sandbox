package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.document.DocumentDto;
import com.scnsoft.eldermark.mobile.dto.document.DocumentListItemDto;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.List;

public interface ClientDocumentFacade {
    Page<DocumentListItemDto> find(MobileDocumentFilter documentFilter, Pageable pageRequest);

    long count(MobileDocumentFilter documentFilter);

    List<NamedTitledValueEntityDto<Long>> countGroupedBySignatureStatus(MobileDocumentFilter documentFilter);

    DocumentDto findById(Long documentId);

    void download(Long documentId, HttpServletResponse response);

    void downloadCcd(Long clientId, HttpServletResponse response);

    void downloadFacesheet(Long clientId, HttpServletResponse response, ZoneId zoneId);

    String cdaToHtml(Long documentId);

    String clientCcdToHtml(Long clientId);
}
