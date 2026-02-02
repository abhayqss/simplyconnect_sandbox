package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.staffcaseload.StaffCaseloadReport;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class StaffCaseloadWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<StaffCaseloadReport> {

    @Override
    public Workbook generateWorkbook(StaffCaseloadReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addStaffCaseloadTab(workbook, styles, report);
        addStaffCareTeamsTab(workbook, styles, report);
        return workbook;
    }

    public void addStaffCaseloadTab(Workbook wb, Map<String, CellStyle> styles, StaffCaseloadReport report) {
        String sheetName = "Number of individuals on staff caseload";
        var headerValues = List.of(
            "Staff Name",
            "# of individuals on staff caseload",
            "Resident name",
            "Resident ID",
            "Community name",
            "Average score"
        );
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        var reportItems = report.getStaffCaseload();
        for (var item : reportItems) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), item.getEmployeeName());
            writeToCell(styles, row.createCell(colCount++), item.getNumberOfIndividuals());

            for (var residentItem : item.getResidents()) {
                var resColCount = colCount;
                if (row == null) row = sheet.createRow(rowCount++);
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientName());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientId());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getCommunity());
                writeToCellEmptyIfNa(styles, row.createCell(resColCount), formatScore(residentItem.getAverageScore()));
                row = null;
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    public void addStaffCareTeamsTab(Workbook wb, Map<String, CellStyle> styles, StaffCaseloadReport report) {
        String sheetName = "Number of Care Team Members";
        var headerValues = List.of(
                "Staff Name",
                "Contact Status",
                "# of Care Team Members",
                "Resident name",
                "Resident ID",
                "Community name"
        );
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        var reportItems = report.getStaffCareTeams();
        for (var item : reportItems) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), item.getEmployeeName());
            writeToCell(styles, row.createCell(colCount++), item.getEmployeeStatus().getText());
            writeToCell(styles, row.createCell(colCount++), item.getNumberOfCareTeams());

            for (var residentItem : item.getResidents()) {
                var resColCount = colCount;
                if (row == null) row = sheet.createRow(rowCount++);
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientName());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientId());
                writeToCell(styles, row.createCell(resColCount), residentItem.getCommunity());
                row = null;
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private String formatScore(Float score) {
        return score != null ? String.format("%.2f", score) : "";
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.STAFF_CASELOAD;
    }
}
