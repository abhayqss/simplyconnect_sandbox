package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.intune.InTuneReport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class InTuneWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<InTuneReport> {

    private final static String REPORT_SHEET_NAME = "InTune";
    private final static List<String> CLIENT_REPORT_HEADERS = List.of(
        "Client Name",
        "Assessments analyzed",
        "Trigger Question",
        "# of Yes Answers",
        "Dates",
        "# of No answers",
        "Dates"
    );
    private final static List<String> MULTIPLE_CLIENTS_REPORT_HEADERS = List.of(
        "Community",
        "Client Name",
        "Assessments analyzed",
        "Trigger Question",
        "# of Yes Answers",
        "Dates",
        "# of No answers",
        "Dates"
    );

    @Override
    public Workbook generateWorkbook(InTuneReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        if (!report.isSingleClientReport()) {
            addReportingCriteriaTab(workbook, styles, report);
        }
        addInTuneTab(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.IN_TUNE;
    }

    private void addInTuneTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, InTuneReport report) {
        var headerValues = report.isSingleClientReport() ? CLIENT_REPORT_HEADERS : MULTIPLE_CLIENTS_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, REPORT_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getRows();
        for (var itRow : itRows) {

            if (itRow.getClientRows().isEmpty()) {
                continue;
            }

            var colCount = 0;
            var row = sheet.createRow(rowCount);

            if (!report.isSingleClientReport()) {
                writeToCell(styles, row.createCell(colCount++), itRow.getCommunityName());
            }

            for (var clientRow : itRow.getClientRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var clientColCount = colCount;
                writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientName());

                if (clientRow.getQuestions().isEmpty()) {
                    rowCount++;
                    continue;
                }

                writeToCellWithDateList(styles, row.createCell(clientColCount++), clientRow.getAnalyzedAssessmentDates(), report.getTimeZoneOffset());
                for (var question : clientRow.getQuestions()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var questionColCount = clientColCount;
                    writeToCell(styles, row.createCell(questionColCount++), question.getQuestion());
                    writeToCell(styles, row.createCell(questionColCount++), question.getYesAnswerCount());
                    writeToCellWithDateList(styles, row.createCell(questionColCount++), question.getYesAnswerDates(), report.getTimeZoneOffset());
                    writeToCell(styles, row.createCell(questionColCount++), question.getNoAnswerCount());
                    writeToCellWithDateList(styles, row.createCell(questionColCount++), question.getNoAnswerDates(), report.getTimeZoneOffset());
                }
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
