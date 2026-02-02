package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.SDoHReportListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;

public interface SDoHReportFacade {

    Page<SDoHReportListItemDto> find(Long organizationId, Pageable pageable);

    void downloadZip(Long reportId, HttpServletResponse response);

    void downloadXlsx(Long reportId, HttpServletResponse response);

    void markAsSent(Long reportId);

    boolean canMarkAsSent(Long reportId);

    boolean canView();
}
