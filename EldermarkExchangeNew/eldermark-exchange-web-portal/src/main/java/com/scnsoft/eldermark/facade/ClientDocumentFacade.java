package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.EditDocumentDto;
import com.scnsoft.eldermark.dto.UploadClientDocumentDto;
import com.scnsoft.eldermark.dto.document.DocumentDto;
import com.scnsoft.eldermark.dto.document.ClientDocumentListItemDto;
import com.scnsoft.eldermark.dto.filter.ClientDocumentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Collection;

public interface ClientDocumentFacade {

    Page<ClientDocumentListItemDto> find(ClientDocumentFilter documentFilter, Pageable pageRequest);

    DocumentDto findById(Long id);

    void download(Long id, HttpServletResponse response, boolean isViewMode);

    void downloadMultiple(Collection<Long> documentIds, HttpServletResponse response);

    void downloadMultiple(ClientDocumentFilter documentFilter, HttpServletResponse response);

    void downloadFacesheet(Long clientId, HttpServletResponse response, boolean isViewMode, Boolean aggregated, ZoneId zoneId);

    void downloadCcd(Long clientId, HttpServletResponse response, boolean isViewMode, Boolean aggregated);

    String cdaToHtml(Long documentId);

    String clientCcdToHtml(Long clientId, Boolean aggregated);

    Long save(UploadClientDocumentDto uploadDto);

    void deleteById(long id, boolean isTemporary);

    void restoreById(long id);

    Long count(ClientDocumentFilter documentFilter);

    boolean canAdd(Long clientId);

    void downloadServicePlanPdf(Long clientId, HttpServletResponse response, ZoneId zoneId);

    Long edit(EditDocumentDto editDocumentDto);

    Long findOldestDateByClient(Long clientId);
}
