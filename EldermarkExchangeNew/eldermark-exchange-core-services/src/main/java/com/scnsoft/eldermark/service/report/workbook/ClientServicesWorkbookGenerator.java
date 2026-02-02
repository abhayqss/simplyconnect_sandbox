package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.ClientDomainRow;
import com.scnsoft.eldermark.beans.reports.model.ClientServicePlanRow;
import com.scnsoft.eldermark.beans.reports.model.ClientServicesReport;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.CLIENT_SERVICES;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ClientServicesWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ClientServicesReport> {

    @Override
    public Workbook generateWorkbook(ClientServicesReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addServicePlans(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return CLIENT_SERVICES;
    }

    private void addServicePlans(Workbook wb, Map<String, CellStyle> styles, ClientServicesReport report) {
        String sheetName = "Service plans completed";
        var headerValues = Arrays.asList("Community name", "Resident/Client ID", "Resident/Client Name", "Total number of services", "Domain", "Service (resource name)", "Service Coordinator for service", "Target completion date", "Completion date", "Goal status");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<ClientServicePlanRow> servicePlanRowList = report.getServicePlanRows();
        for (var spRow : servicePlanRowList) {

            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), spRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), spRow.getClientId());
            writeToCell(styles, row.createCell(colCount++), spRow.getClientName());
            writeToCell(styles, row.createCell(colCount++), spRow.getTotalNumberOfServices());


            var domainMap = spRow.getDomainRows().stream()
                    .collect(groupingBy(ClientDomainRow::getDomainName));

            var domainNum = 0;
            var domainColCount = 3;
            for (var entry : domainMap.entrySet()) {
                if (domainNum != 0) {
                    row = sheet.createRow(rowCount++);
                }
                writeToCell(styles, row.createCell(domainColCount++), entry.getKey());
                var goalRows = entry.getValue().stream().flatMap(d -> d.getGoalRows().stream()).collect(toList());
                int goalNum = 0;
                for (var goal : goalRows) {
                    if (goalNum != 0) {
                        row = sheet.createRow(rowCount++);
                    }
                    writeToCell(styles, row.createCell(domainColCount++), goal.getResourceName());
                    writeToCell(styles, row.createCell(domainColCount++), spRow.getCoordinatorName());
                    writeToCell(styles, row.createCell(domainColCount++), ofNullable(goal.getTargetCompletionDate()).map(d -> formatToDate(d, report.getTimeZoneOffset())).orElse(null));
                    writeToCell(styles, row.createCell(domainColCount++), ofNullable(goal.getCompletionDate()).map(d -> formatToDate(d, report.getTimeZoneOffset())).orElse(null));
                    writeToCell(styles, row.createCell(domainColCount++), goal.getGoalStatus());
                    domainColCount = 4;
                    goalNum++;
                }
                domainColCount = 3;
                domainNum++;
            }

        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

}
