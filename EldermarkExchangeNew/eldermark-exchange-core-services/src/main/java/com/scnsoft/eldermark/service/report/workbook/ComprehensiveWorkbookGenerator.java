package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveReport;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveReportRecord;
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
public class ComprehensiveWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ComprehensiveReport>{

    @Override
    public Workbook generateWorkbook(ComprehensiveReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addComprehensiveAssessmentDetails(workbook, styles, report);
        return workbook;
    }

    private void addComprehensiveAssessmentDetails(Workbook wb, Map<String, CellStyle> styles, ComprehensiveReport report){
        String sheetName = "Comprehensive assessment";
        var headerValues = Arrays.asList("Community Name", "Client ID", "Assessment status", "Response");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        Map<String, List<ComprehensiveReportRecord>> comprehensiveRecordsList = report.getComprehensiveReportRecords();
        for (Map.Entry<String, List<ComprehensiveReportRecord>> entry : comprehensiveRecordsList.entrySet()) {

            var recordCount = 0;
            List<ComprehensiveReportRecord> comprehensiveReportRecordList = entry.getValue();

            int colCount;
            for (ComprehensiveReportRecord comprehensiveRecord : comprehensiveReportRecordList) {
                var row = sheet.createRow(rowCount++);
                if (recordCount == 0){
                    colCount = 0;
                    writeToCell(styles, row.createCell(colCount++), comprehensiveRecord.getCommunityName());
                } else {
                    colCount = 1;
                }
                writeToCell(styles, row.createCell(colCount++), comprehensiveRecord.getClientId());
                writeToCell(styles, row.createCell(colCount++), comprehensiveRecord.getAssessmentStatus());
                writeToCell(styles, row.createCell(colCount++), comprehensiveRecord.getAssessmentResponse());
                recordCount ++;
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.COMPREHENSIVE;
    }
}
