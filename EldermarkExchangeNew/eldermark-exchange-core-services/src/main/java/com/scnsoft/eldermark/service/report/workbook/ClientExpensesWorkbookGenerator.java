package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.expenses.ClientExpensesReport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ClientExpensesWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ClientExpensesReport> {

    @Override
    public Workbook generateWorkbook(ClientExpensesReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addClientExpensesTab(workbook, styles, report);
        return workbook;
    }

    public void addClientExpensesTab(Workbook wb, Map<String, CellStyle> styles, ClientExpensesReport report) {
        String sheetName = "Client Expenses";
        var headerValues = List.of(
                "Community Name",
                "Client ID",
                "Client Status",
                "Client Name",
                "Expense Type",
                "Cost of Expense",
                "Cumulative Cost of Expense",
                "Expense Date",
                "Comment",
                "Date Reported",
                "Author"
        );
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var item: report.getItems()) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles,  row.createCell(colCount++), item.getCommunityName());

            for (var clientItem: item.getClients()) {
                var clientColCount = colCount;
                if (row == null) row = sheet.createRow(rowCount++);
                writeToCell(styles, row.createCell(clientColCount++), clientItem.getClientId());
                writeToCell(styles, row.createCell(clientColCount++), clientItem.getIsClientActive() ? "Active" : "Inactive");
                writeToCell(styles, row.createCell(clientColCount++), clientItem.getClientName());

                for (var expenseItem: clientItem.getExpenses()) {
                    var expenseColCount = clientColCount;
                    if (row == null) row = sheet.createRow(rowCount++);
                    writeToCell(styles, row.createCell(expenseColCount++), expenseItem.getType().getDisplayName());
                    writeCostToCell(styles, row.createCell(expenseColCount++), expenseItem.getCost());
                    writeCostToCell(styles, row.createCell(expenseColCount++), expenseItem.getCumulativeCost());
                    writeToCell(styles, row.createCell(expenseColCount++), formatToDate(expenseItem.getDate(), report.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(expenseColCount++), expenseItem.getComment());
                    writeToCell(styles, row.createCell(expenseColCount++), formatToDate(expenseItem.getReportedDate(), report.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(expenseColCount++), expenseItem.getAuthor());
                    row = null;
                }
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void writeCostToCell(Map<String, CellStyle> styles, Cell cell, Long cost) {
        writeToCell(styles, cell, String.format("%d.%02d", cost / 100, cost % 100));
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.CLIENT_EXPENSES;
    }
}
