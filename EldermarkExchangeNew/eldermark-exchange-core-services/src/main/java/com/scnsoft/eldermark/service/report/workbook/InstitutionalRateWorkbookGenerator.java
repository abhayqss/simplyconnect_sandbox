package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.InstitutionalRateReport;
import com.scnsoft.eldermark.beans.reports.model.InstitutionalRateReportRow;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.autosizeWidth;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCell;

@Service
public class InstitutionalRateWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<InstitutionalRateReport> {

    @Override
    public Workbook generateWorkbook(InstitutionalRateReport report) {
        var workbook = new XSSFWorkbook();
        var styles = ExcelUtils.createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addInstitutionalRatesTab(workbook, styles, report);
        return workbook;
    }

    public void addInstitutionalRatesTab(Workbook wb, Map<String, CellStyle> styles, InstitutionalRateReport report) {
        String sheetName = "Institutional Rate (ER, SNF, Hospital)";
        var headerValues = List.of(
            "Community Name",
            "# of active residents",
            "# of ER visits",
            "# of SNF institutionalizations",
            "# of hospitalizations",
            "Institutional rate, %",
            "ER rate, %",
            "SNF rate, %",
            "Hospital rate, %"
        );
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<InstitutionalRateReportRow> eventRows = report.getRows();
        for (var eventRow : eventRows) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), eventRow.getCommunity());
            writeToCell(styles, row.createCell(colCount++), eventRow.getActiveResidentCount());
            writeToCell(styles, row.createCell(colCount++), eventRow.getErVisitCount());
            writeToCell(styles, row.createCell(colCount++), eventRow.getSnfInstitutionalizationCount());
            writeToCell(styles, row.createCell(colCount++), eventRow.getHospitalizationCount());
            writeToCell(styles, row.createCell(colCount++), formatFloat(eventRow.getInstitutionalRate()));
            writeToCell(styles, row.createCell(colCount++), formatFloat(eventRow.getErRate()));
            writeToCell(styles, row.createCell(colCount++), formatFloat(eventRow.getSnfRate()));
            writeToCell(styles, row.createCell(colCount++), formatFloat(eventRow.getHospitalRate()));
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.INSTITUTIONAL_RATE;
    }

    private String formatFloat(Float value) {
        return String.format("%.2f", value);
    }
}
