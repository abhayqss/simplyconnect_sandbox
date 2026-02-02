package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.ClientDemographicInfo;
import com.scnsoft.eldermark.dump.model.ClientDemographicsDump;
import com.scnsoft.eldermark.dump.model.DumpType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClientDemographicsExcelDumpWriter extends ExcelDumpWriter implements DumpWriter<ClientDemographicsDump> {

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_DEMOGRAPHICS;
    }

    private static final ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CST"));

    @Override
    public void writeDump(ClientDemographicsDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    Workbook generateWorkBook(ClientDemographicsDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);

        var splitDate = LocalDate.of(2019, Month.JULY, 1).atStartOfDay(zoneId).toInstant();

        var intakeStart = LocalDate.of(2020, 1, 1).atStartOfDay(zoneId).toInstant();
        var intakeEnd = LocalDate.of(2020, 6, 30).atTime(LocalTime.MAX).atZone(zoneId).toInstant();

        var partitioned = dump.getDemographicList().stream()
                .filter(d -> d.getIntakeDate() == null || d.getIntakeDate().isAfter(intakeStart) && d.getIntakeDate().isBefore(intakeEnd))
                .collect(Collectors.partitioningBy((d) -> d.getIntakeDate() == null));

//        01/01/2020 - 06/30/2020

        addClientDemographicsInfoInfo(workbook, styles, "Intake 01-01-2020 - 06-30-2020",
                partitioned.get(Boolean.FALSE));

        addClientDemographicsInfoInfo(workbook, styles, "Without intake date",
                partitioned.get(Boolean.TRUE));

        addClientDemographicsInfoInfo(workbook, styles, "All clients",
                dump.getDemographicList());
        return workbook;
    }

    private void addClientDemographicsInfoInfo(XSSFWorkbook wb, Map<String, CellStyle> styles, String sheetName,
                                               List<ClientDemographicInfo> clientDemographicInfoList) {
        var sheet = wb.createSheet(sheetName);

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        var headerValues = Arrays.asList("ID",
                "Community name",
                "Client name",
                "Record status",
                "Intake date (CST/CDT)",
//                "Admit date",
                "Date of birth",
                "Gender",
                "Race",
                "State",
                "City",
                "Zip code",
                "Street");

        writeHeader(headerRow, styles, headerValues);

        for (var value : clientDemographicInfoList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getResidentId());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.getFirstName() + " " + value.getLastName());
            writeToCell(styles, row.createCell(colCount++), value.isActive() ? "active" : "inactive");
            writeToCell(styles, row.createCell(colCount++), formatToDateTime(value.getIntakeDate(), zoneId));
//            writeToCell(styles, row.createCell(colCount++), formatToDateTime(value.getAdmitDate(), zoneId));
            writeToCell(styles, row.createCell(colCount++), formatToDate(value.getBirthDate()));
            writeToCell(styles, row.createCell(colCount++), value.getGender());
            writeToCell(styles, row.createCell(colCount++), value.getRace());
            writeToCell(styles, row.createCell(colCount++), value.getAddress().getState());
            writeToCell(styles, row.createCell(colCount++), value.getAddress().getCity());
            writeToCell(styles, row.createCell(colCount++), value.getAddress().getZip());
            writeToCell(styles, row.createCell(colCount++), value.getAddress().getStreet());
        }

        autosizeWidth(sheet, headerValues.size());
    }
}
