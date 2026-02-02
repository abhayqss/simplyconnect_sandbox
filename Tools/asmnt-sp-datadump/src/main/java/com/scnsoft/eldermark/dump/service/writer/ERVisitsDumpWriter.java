package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Map;

import static java.util.Arrays.asList;

@Component
public class ERVisitsDumpWriter extends ExcelDumpWriter implements DumpWriter<ERVisitsDump> {

    @Override
    public void writeDump(ERVisitsDump dump) {
        //writeToConsole(dump);
        Workbook workbook = generateWorkbook(dump);
        writeToFile(workbook, dump);
    }

    public Workbook generateWorkbook(ERVisitsDump report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addHospitalizationsTab(workbook, styles, report);
        return workbook;
    }

    public void addHospitalizationsTab(Workbook wb, Map<String, CellStyle> styles, ERVisitsDump report) {
        String sheetName = "ER Visits";
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Date of Institutionalization",
                "Location", "Situation", "Background", "Assessment", "Injury", "Follow up expected", "# of ED visits in the last six month",
                "Source");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var eventRow: report.getEventRows()) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;

            writeToCell(styles,  row.createCell(colCount++), eventRow.getCommunityName());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getClientName());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getClientId());
            writeToCell(styles,  row.createCell(colCount++), formatToDateTime(eventRow.getDateOfInstitutionalization(), ZoneId.systemDefault()));
            writeToCell(styles,  row.createCell(colCount++), eventRow.getLocation());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getSituation());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getBackground());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getAssessment());
            writeToCell(styles,  row.createCell(colCount++), String.valueOf(eventRow.isInjury()));
            writeToCell(styles,  row.createCell(colCount++), String.valueOf(eventRow.isFollowup()));
            writeToCell(styles,  row.createCell(colCount++), eventRow.getNumberOfEdVisits());
            writeToCell(styles,  row.createCell(colCount++), eventRow.getSource().getDisplay());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.ER_VISITS;
    }
}
