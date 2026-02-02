package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.ReportTypeDto;
import com.scnsoft.eldermark.dto.report.ReportFilterDto;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ReportsFacade {

    void downloadReport(ReportFilterDto filter, HttpServletResponse response);

    List<ReportTypeDto> getAvailableReportTypes(Long organizationId, List<Long> communityIds);
}
