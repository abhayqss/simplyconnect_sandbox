package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.ClientIntakesReport;
import com.scnsoft.eldermark.beans.reports.model.ClientIntakesReportRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.CLIENT_INTAKES;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ClientIntakesWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ClientIntakesReport> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public Workbook generateWorkbook(ClientIntakesReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        fillWorkbook(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return CLIENT_INTAKES;
    }

    private void fillWorkbook(Workbook wb, Map<String, CellStyle> styles, ClientIntakesReport report) {
        var intakesHeaderValues = Arrays.asList("Resident/Client ID", "Client name", "Community name", "Intake date",
            "Comment", "Record status", "Deactivate Date","Exit Date", "Reason", "Comment", "DOB", "Gender", "Race", "City", "Insurance network",
            "Insurance Plan", "Date Created");
        var allHeaderValues = Arrays.asList("Resident/Client ID", "Client name", "Community name", "Intake date",
            "Deactivate Date", "Exit date", "Reason", "Comment", "Record status", "DOB", "Gender", "Race", "City", "Insurance network",
            "Insurance Plan", "Date Created");
        createAndFillSheet(
            "Records with intake date",
            wb,
            styles,
            report.getIntakeWithinDatesRows(),
            report.getTimeZoneOffset(),
            intakesHeaderValues
        );

        createAndFillAllRecordsSheet(
            "All records",
            wb,
            styles,
            report.getAllRows(),
            report.getTimeZoneOffset(),
            allHeaderValues
        );
    }

    private void createAndFillSheet(
        String sheetName,
        Workbook wb,
        Map<String, CellStyle> styles,
        List<ClientIntakesReportRow> reportRows,
        Integer timeZoneOffset,
        List<String> headerValues
    ) {

        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var reportRow : reportRows) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;

            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getClientId());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getClientName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getCommunityName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getIntakeDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getIntakeComment());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getStatus());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getDeactivatedDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getExitDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getDeactivationReason());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getExitComment());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getBirthDate(), DATE_FORMAT));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getGender());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getRace());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getCity());
            if (!StringUtils.isEmpty(reportRow.getInsuranceNetwork())) {
                writeToCellEmptyIfNa(styles, row.createCell(colCount), reportRow.getInsuranceNetwork());
                for (String plan : reportRow.getHealthPlans()) {
                    writeToCellEmptyIfNa(styles, sheet.createRow(rowCount++).createCell(colCount), plan);
                }

            } else {
                for (int i = 0; i < reportRow.getHealthPlans().size(); i++) {
                    if (i == 0) {
                        writeToCellEmptyIfNa(styles, row.createCell(colCount), reportRow.getHealthPlans().get(i));
                    } else {
                        writeToCellEmptyIfNa(styles, sheet.createRow(rowCount++).createCell(colCount), reportRow.getHealthPlans().get(i));
                    }

                }
            }
            colCount++;
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getInsurancePlan());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getCreatedDate(), timeZoneOffset));
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void createAndFillAllRecordsSheet(
        String sheetName,
        Workbook wb,
        Map<String, CellStyle> styles,
        List<ClientIntakesReportRow> reportRows,
        Integer timeZoneOffset,
        List<String> headerValues
    ) {

        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var reportRow : reportRows) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;

            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getClientId());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getClientName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getCommunityName());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getIntakeDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getDeactivatedDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getExitDate(), timeZoneOffset));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getDeactivationReason());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getExitComment());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getStatus());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getBirthDate(), DATE_FORMAT));
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getGender());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getRace());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getCity());
            if (!StringUtils.isEmpty(reportRow.getInsuranceNetwork())) {
                writeToCellEmptyIfNa(styles, row.createCell(colCount), reportRow.getInsuranceNetwork());
                for (String plan : reportRow.getHealthPlans()) {
                    writeToCellEmptyIfNa(styles, sheet.createRow(rowCount++).createCell(colCount), plan);
                }

            } else {
                for (int i = 0; i < reportRow.getHealthPlans().size(); i++) {
                    if (i == 0) {
                        writeToCellEmptyIfNa(styles, row.createCell(colCount), reportRow.getHealthPlans().get(i));
                    } else {
                        writeToCellEmptyIfNa(styles, sheet.createRow(rowCount++).createCell(colCount), reportRow.getHealthPlans().get(i));
                    }

                }
            }
            colCount++;
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), reportRow.getInsurancePlan());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), formatToDate(reportRow.getCreatedDate(), timeZoneOffset));
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
