package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.TimeToCompleteReport;
import com.scnsoft.eldermark.beans.reports.model.TimeToCompleteResult;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class TimeToCompleteWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<TimeToCompleteReport> {

    @Override
    public Workbook generateWorkbook(TimeToCompleteReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addComprehensiveAssessmentDetails(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.TIME_TO_COMPLETE_ASSESSMENT;
    }

    private void addComprehensiveAssessmentDetails(Workbook wb, Map<String, CellStyle> styles, TimeToCompleteReport report) {
        String sheetName = "Time to Complete Assessment";
        var headerValues = Arrays.asList("Community Name", "Resident/Client ID", "Resident/Client Name", "Start Date of Assessment", "End Date of Assessment", "Who Completed Assessment", "Time to complete assessment, minutes");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<TimeToCompleteResult> timeToCompleteResultList = report.getTimeToCompleteResultList();
        for (var timeToCompleteResult : timeToCompleteResultList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), timeToCompleteResult.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), timeToCompleteResult.getClientId());
            writeToCell(styles, row.createCell(colCount++), timeToCompleteResult.getClientName());
            writeToCell(styles, row.createCell(colCount++), formatToDateTime(timeToCompleteResult.getAssessmentStartDate(), report.getTimeZoneOffset()));
            writeToCell(styles, row.createCell(colCount++), formatToDateTime(timeToCompleteResult.getAssessmentEndDate(), report.getTimeZoneOffset()));
            writeToCell(styles, row.createCell(colCount++), timeToCompleteResult.getClientName()); //todo [reports] is it correct?
            writeToCell(styles, row.createCell(colCount++), timeToCompleteResult.getTimeToComplete());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

}
