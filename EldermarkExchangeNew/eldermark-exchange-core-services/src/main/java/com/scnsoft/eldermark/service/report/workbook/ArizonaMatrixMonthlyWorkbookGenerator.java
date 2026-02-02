package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixMonthlyReport;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixMonthlyReportRow;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ArizonaMatrixMonthlyWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ArizonaMatrixMonthlyReport> {

    public static final String MISSING_TAB_NAME = "Missing assessments";
    public static final String UPCOMING_TAB_NAME = "Upcoming assessments";
    public static final List<String> TAB_HEADERS = List.of(
        "Community Name",
        "Client name",
        "Client ID",
        "Assessment Date",
        "Total Score",
        "Completed By",
        "Follow Up",
        "Scheduled Follow Up"
    );

    @Override
    public Workbook generateWorkbook(ArizonaMatrixMonthlyReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addAssessmentResultsTab(workbook, styles, MISSING_TAB_NAME, report.getMissing(), report.getTimeZoneOffset());
        addAssessmentResultsTab(workbook, styles, UPCOMING_TAB_NAME, report.getUpcoming(), report.getTimeZoneOffset());
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.ARIZONA_MATRIX_MONTHLY;
    }

    private void addAssessmentResultsTab(Workbook wb, Map<String, CellStyle> styles, String name, List<ArizonaMatrixMonthlyReportRow> rows, Integer timezoneOffset) {
        Sheet sheet = createSheetWithHeader(wb, styles, name, TAB_HEADERS, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, TAB_HEADERS.size() - 1));

        var rowCount = 1;
        for (var reportRow : rows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount++);
            writeToCell(styles, row.createCell(colCount++), reportRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), reportRow.getClientName());
            writeToCell(styles, row.createCell(colCount++), reportRow.getClientId());
            writeToCell(styles, row.createCell(colCount++), formatToDate(reportRow.getAssessmentDate(), timezoneOffset));
            writeToCell(styles, row.createCell(colCount++), reportRow.getTotalScore());
            writeToCell(styles, row.createCell(colCount++), reportRow.getCompletedBy());
            writeToCell(styles, row.createCell(colCount++), reportRow.getFollowUp());
            writeToCell(styles, row.createCell(colCount++), formatToDate(reportRow.getFollowUpDate(), timezoneOffset));
        }

        autosizeWidth(sheet, TAB_HEADERS.size() + 1);
    }
}
