package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.DomainRow;
import com.scnsoft.eldermark.beans.reports.model.ServicePlanReport;
import com.scnsoft.eldermark.beans.reports.model.ServicePlanRow;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.SERVICE_PLANS;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ServicePlanWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ServicePlanReport> {

    @Override
    public Workbook generateWorkbook(ServicePlanReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addServicePlans(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return SERVICE_PLANS;
    }

    private void addServicePlans(Workbook wb, Map<String, CellStyle> styles, ServicePlanReport report) {
        String sheetName = "Service plans completed";
        var headerValues = Arrays.asList("Community name", "Resident/Client ID", "Resident/Client Name", "Service Coordinator", "Date Service Plan Completed", "Service plan status", "List of each domain included", "List of goals included", "List of resources for each goal", "Goal status");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<ServicePlanRow> servicePlanRowList = report.getServicePlanRowList();
        for (var spRow : servicePlanRowList){
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles,  row.createCell(colCount++), spRow.getCommunityName());
            writeToCell(styles,  row.createCell(colCount++), spRow.getClientId());
            writeToCell(styles,  row.createCell(colCount++), spRow.getClientName());
            writeToCell(styles,  row.createCell(colCount++), spRow.getServiceCoordinator());
            writeToCell(styles,  row.createCell(colCount++), ofNullable(spRow.getDateCompleted())
                    .map(date -> formatToDate(date, report.getTimeZoneOffset()))
                    .orElse(null));
            writeToCell(styles,  row.createCell(colCount++), spRow.getServicePlanStatus());
            writeToCell(styles,  row.createCell(colCount++), spRow.getTotalNumberOfDomains());
            writeToCell(styles,  row.createCell(colCount++), spRow.getTotalNumberOfGoals());
            writeToCell(styles,  row.createCell(colCount++), spRow.getTotalNumberOfResources());


            var goalColCount = 5;
            var startGroupRowCount = rowCount;
            var domainMap= spRow.getDomainRows().stream()
                    .collect(groupingBy(DomainRow::getDomainName));

            for (var entry : domainMap.entrySet()){
                row = sheet.createRow(rowCount++);
                writeToCell(styles,  row.createCell(goalColCount++), entry.getKey() );
                var goalRows = entry.getValue().stream().flatMap(d -> d.getGoalList().stream()).collect(toList());
                int goalNum = 0;
                for (var goal: goalRows){
                    if (goalNum != 0){
                        row = sheet.createRow(rowCount++);
                    }
                    writeToCell(styles,  row.createCell(goalColCount++), goal.getGoalName());
                    writeToCell(styles,  row.createCell(goalColCount++), goal.getResourceName());
                    writeToCell(styles,  row.createCell(goalColCount++), goal.getGoalStatus());
                    goalColCount = 6;
                    goalNum++;
                }
                goalColCount = 5;
            }
            var endGroupRowCount = rowCount;

            sheet.groupRow( startGroupRowCount++,endGroupRowCount);
            sheet.setRowGroupCollapsed(endGroupRowCount, true);
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

}
