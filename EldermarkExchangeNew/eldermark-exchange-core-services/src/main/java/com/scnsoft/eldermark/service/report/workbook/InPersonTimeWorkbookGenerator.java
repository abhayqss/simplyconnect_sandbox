package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.model.InPersonTimeReport;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.IN_PERSON_TIME;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.createStyles;

@Service
public class InPersonTimeWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<InPersonTimeReport> {

    @Override
    public Workbook generateWorkbook(InPersonTimeReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addFirstTab(workbook, styles, report);
        addSecondTab(workbook, styles, report);
        addTotalClientsTab(workbook, styles, report.getTotalClientsTabList());
        addTotalServiceCoordinatorsTab(workbook, styles, report.getTotalServiceCoordinatorsTabList());
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return IN_PERSON_TIME;
    }

    private void addFirstTab(Workbook wb, Map<String, CellStyle> styles, InPersonTimeReport report){
        var sheet = wb.createSheet("In person time with individuals");
        var headerValues = Arrays.asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Total Time spent with individual, minutes", "Service Coordinator Name ");
        addEncNoteFirstTab(sheet, headerValues, styles, report.getFirstTabList());
     }

    private void addSecondTab(Workbook wb, Map<String, CellStyle> styles, InPersonTimeReport report){
        var sheet = wb.createSheet("Sessions");
        var headerValues = Arrays.asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Date of session", "Time spent, minutes", "Service Coordinator Name", "Subjective", "Objective", "Assessment", "Plan");
        addEncNoteSecondTab(sheet, headerValues, styles, report.getSecondTabList(), report.getTimeZoneOffset());
    }

}
