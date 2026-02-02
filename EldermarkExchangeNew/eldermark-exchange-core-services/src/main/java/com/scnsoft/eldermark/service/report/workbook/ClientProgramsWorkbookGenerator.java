package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.ClientProgramsReport;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ClientProgramsWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ClientProgramsReport> {

    @Override
    public Workbook generateWorkbook(ClientProgramsReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addClientPrograms(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.CLIENT_PROGRAMS;
    }

    private void addClientPrograms(Workbook wb, Map<String, CellStyle> styles, ClientProgramsReport report) {
        String sheetName = "Client Programs";
        var headerValues = Arrays
                .asList("Client ID", "Client Name", "Community name", "Program name", "Service Provider for Program", "Start Date of Program", "End Date of Program");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var clientProgramsRows = report.getClientProgramsRows();
        for (var cpRow : clientProgramsRows) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), cpRow.getClientId());
            writeToCell(styles, row.createCell(colCount++), cpRow.getClientName());
            writeToCell(styles, row.createCell(colCount++), cpRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), cpRow.getProgramName());
            writeToCell(styles, row.createCell(colCount++), cpRow.getServiceProvider());
            writeToCell(styles, row.createCell(colCount++), formatToDate(cpRow.getStartDate(), report.getTimeZoneOffset()));
            writeToCell(styles, row.createCell(colCount), formatToDate(cpRow.getEndDate(), report.getTimeZoneOffset()));
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
