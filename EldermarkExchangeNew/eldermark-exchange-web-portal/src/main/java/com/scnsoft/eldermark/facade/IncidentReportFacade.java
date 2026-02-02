package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IncidentReportHistoryListItemDto;
import com.scnsoft.eldermark.dto.IncidentReportListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;


public interface IncidentReportFacade {

    Page<IncidentReportListItemDto> find(IncidentReportFilter filter, Pageable pageable);

    IncidentReportDto findDefault(Long eventId);

    IncidentReportDto findById(Long id);

    Page<IncidentReportHistoryListItemDto> findHistoryById(Long id, Pageable pageable);

    Long saveDraft(IncidentReportDto incidentReportDto);

    Long submit(IncidentReportDto incidentReportDto);

    void downloadById(Long id, HttpServletResponse response, ZoneId zoneId);

    boolean canViewList();

    boolean canViewByClientId(Long clientId);

    FileBytesDto downloadIncidentPictureById(Long pictureId);

    void deleteById(Long id);

    Long findOldestDateByOrganization(Long orgId);

    Long findNewestDateByOrganization(Long orgId);

    void joinConversation(Long id);
}
