package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.model.PhoneCallTimeReport;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.Arrays.asList;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.PHONE_CALL_TIME;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.createStyles;

@Service
public class PhoneCallTimeWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<PhoneCallTimeReport> {

    @Override
    public Workbook generateWorkbook(PhoneCallTimeReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addFirstTab(workbook, styles, report);
        addSecondTab(workbook, styles, report);
        addTotalClientsTab(workbook, styles, report.getTotalClientsTabList());
        addTotalServiceCoordinatorsTab(workbook, styles, report.getTotalServiceCoordinatorsTabList());
        return workbook ;
    }

    @Override
    public ReportType generatedReportType() {
        return PHONE_CALL_TIME;
    }

    private void addFirstTab(Workbook wb, Map<String, CellStyle> styles, PhoneCallTimeReport report){
        var sheet = wb.createSheet("Phone call time with individual");
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Total time spent on Phone calls, minutes", "Service Coordinator Name ");
        addEncNoteFirstTab(sheet, headerValues, styles, report.getFirstTabList());
    }

    private void addSecondTab(Workbook wb, Map<String, CellStyle> styles, PhoneCallTimeReport report){
        var sheet = wb.createSheet("Sessions");
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Call date", "Time spent, minutes", "Service Coordinator Name", "Subjective", "Objective", "Assessment", "Plan");
        addEncNoteSecondTab(sheet, headerValues, styles, report.getSecondTabList(), report.getTimeZoneOffset());
    }

}
