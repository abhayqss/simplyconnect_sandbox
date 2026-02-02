package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;
import org.apache.poi.ss.usermodel.Workbook;

public interface WorkbookGenerator<R extends Report> {

    Workbook generateWorkbook(R report);

    ReportType generatedReportType();
}
