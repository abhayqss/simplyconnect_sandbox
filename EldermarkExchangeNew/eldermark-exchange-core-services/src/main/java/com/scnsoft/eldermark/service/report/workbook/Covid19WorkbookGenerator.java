package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Covid19Report;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotCacheDefinition;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.COVID_19_LOG;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class Covid19WorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<Covid19Report> {

    private static final String DETAIL_SHEET = "Detail";
    private static final String SUMMARY_SHEET = "Summary";
    private static final String COVID_CELL_STYLE = "covid_cell";
    private static final int SKIP_DETAIL_ROWS = 1;
    private static final int SKIP_DETAIL_COLS = 0;

    @Value("classpath:reports/COVID-19_Log_template_cleaned.xlsx")
    private Resource covid19Template;

    @Override
    public Workbook generateWorkbook(Covid19Report report) {
        var workbook = openTemplateWorkbook();
        var styles = createStyles(workbook);
        addSpecificStyles(styles, workbook);
        fillDetailTab(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return COVID_19_LOG;
    }


    private XSSFWorkbook openTemplateWorkbook() {
        try {
            return (XSSFWorkbook) WorkbookFactory.create(covid19Template.getInputStream());
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.COVID_19_IO_ERROR);
        }
    }

    private void addSpecificStyles(Map<String, CellStyle> styles, Workbook workbook) {
        var inputSheet = workbook.getSheet(DETAIL_SHEET);
        var cell = inputSheet.getRow(SKIP_DETAIL_ROWS).getCell(SKIP_DETAIL_COLS);
        styles.put(COVID_CELL_STYLE, cell.getCellStyle());

    }

    private void fillDetailTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, Covid19Report report) {
        var rowCount = SKIP_DETAIL_ROWS;
        var sheet = workbook.getSheet(DETAIL_SHEET);
        workbook.setActiveSheet(workbook.getSheetIndex(DETAIL_SHEET));
        var colCount = 0;
        for (var reportRow : report.getRows()) {
            var row = sheet.createRow(rowCount++);
            colCount = SKIP_DETAIL_COLS;

            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getSpecimenDate(), report.getTimeZoneOffset()));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getCommunityName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getReason());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getClientName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getResultDate(), report.getTimeZoneOffset()));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getResult());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getNotifiedDate(), report.getTimeZoneOffset()));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getComment());
            writeToCellEmptyIfNa(styles, row.createCell(colCount), formatToMonthYear(reportRow.getSpecimenDate(), report.getTimeZoneOffset()));
        }
        autosizeWidth(sheet, colCount + SKIP_DETAIL_COLS + 1);

        if (rowCount > SKIP_DETAIL_ROWS) {
            updatePivotTableDataSource(workbook, rowCount, colCount);
            applyStyling(workbook, styles, rowCount);
        }
    }

    private void updatePivotTableDataSource(XSSFWorkbook workbook, int rowCount, int colCount) {
        var sheet = workbook.getSheet(SUMMARY_SHEET);
        if (sheet.getPivotTables().size() > 0) {
            var pivotTable = sheet.getPivotTables().get(0);
            for (org.apache.poi.ooxml.POIXMLDocumentPart documentPart : pivotTable.getRelations()) {
                if (documentPart instanceof XSSFPivotCacheDefinition) {
                    var pivotCacheDefinition = (XSSFPivotCacheDefinition) documentPart;
                    var CTPivotCacheDefinition = pivotCacheDefinition.getCTPivotCacheDefinition();
                    String fromCell = CellReference.convertNumToColString(SKIP_DETAIL_COLS) + (SKIP_DETAIL_COLS + 1);
                    String toCell = CellReference.convertNumToColString(colCount) + rowCount;
                    CTPivotCacheDefinition.getCacheSource().getWorksheetSource().setRef(fromCell + ":" + toCell);
                }
            }
        }
    }

    private void applyStyling(XSSFWorkbook workbook, Map<String, CellStyle> styles, int rowCount) {
        var sheet = workbook.getSheet(DETAIL_SHEET);
        for (int rowIdx = SKIP_DETAIL_ROWS; rowIdx < rowCount; ++rowIdx) {
            var row = sheet.getRow(rowIdx);
            for (int colIdx = SKIP_DETAIL_COLS; colIdx < row.getLastCellNum(); ++colIdx) {
                row.getCell(colIdx).setCellStyle(styles.get(COVID_CELL_STYLE));
            }
        }

        var table = sheet.getTables().get(0);
        var newTableArea = new AreaReference(
                table.getArea().getFirstCell(),
                new CellReference(rowCount - 1, sheet.getRow(rowCount - 1).getLastCellNum() - 1),
                workbook.getSpreadsheetVersion()
        );
        table.setArea(newTableArea);
    }
}
