package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.model.HospitalizationEventRow;
import com.scnsoft.eldermark.dump.model.HospitalizationsDump;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Component
public class HospitalizationsDumpWriter extends ExcelDumpWriter implements DumpWriter<HospitalizationsDump> {

    @Override
    public void writeDump(HospitalizationsDump dump) {
        //writeToConsole(dump);
        Workbook workbook = generateWorkbook(dump);
        writeToFile(workbook, dump);
    }

    public Workbook generateWorkbook(HospitalizationsDump report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addHospitalizationsTab(workbook, styles, report);
        return workbook;
    }

    public void addHospitalizationsTab(Workbook wb, Map<String, CellStyle> styles, HospitalizationsDump report) {
        String sheetName = "Hospitalizations";
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "Date of Institutionalization",
                "Location", "Situation", "Background", "Assessment", "Injury", "Follow up expected", "Source");
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
            writeToCell(styles,  row.createCell(colCount++), eventRow.getSource().getDisplay());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void writeRow(Row row, Map<String, CellStyle> styles, HospitalizationEventRow eventRow, String source) {



    }

    @Override
    public DumpType getDumpType() {
        return DumpType.HOSPITALIZATIONS;
    }
}
