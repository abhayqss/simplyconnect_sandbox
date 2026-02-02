package com.scnsoft.eldermark.service.report.sdoh;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHReport;
import org.apache.poi.ss.usermodel.Workbook;

import java.time.ZoneId;

public interface SDoHWorkbookGenerator {
    Workbook generateWorkbook(SDoHReport report, ZoneId zoneId);
}
