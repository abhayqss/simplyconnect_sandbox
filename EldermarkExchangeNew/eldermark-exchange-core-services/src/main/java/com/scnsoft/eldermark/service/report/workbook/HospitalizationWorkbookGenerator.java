package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.model.HospitalizationEventRow;
import com.scnsoft.eldermark.beans.reports.model.HospitalizationsReport;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.HOSPITALIZATIONS;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;
import static java.util.Arrays.asList;

@Service
public class HospitalizationWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<HospitalizationsReport> {

    @Override
    public Workbook generateWorkbook(HospitalizationsReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addHospitalizationsTab(workbook, styles, report);
        return workbook;
    }

    public void addHospitalizationsTab(Workbook wb, Map<String, CellStyle> styles, HospitalizationsReport report) {
        String sheetName = "Hospitalizations";
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Date of Institutionalization",
                "Location", "Situation", "Background", "Assessment", "Injury", "Follow up expected", "Source");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<HospitalizationEventRow> eventsRowList = report.getEventRows();
        for (var eventRow: eventsRowList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles,  row.createCell(colCount++), eventRow.getCommunityName());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getClientName());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getClientId());
            writeToCell(styles,  row.createCell(colCount++), formatToDateTime(eventRow.getDateOfInstitutionalization(), report.getTimeZoneOffset()));
            writeToCell(styles,  row.createCell(colCount++), eventRow.getLocation());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getSituation());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getBackground());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getAssessment());
            writeToCell(styles,  row.createCell(colCount++), String.valueOf(eventRow.isInjury()));
            writeToCell(styles,  row.createCell(colCount++), String.valueOf(eventRow.isFollowup()));
            writeToCell(styles,  row.createCell(colCount++), eventRow.getSource().getDisplay());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return HOSPITALIZATIONS;
    }
}
