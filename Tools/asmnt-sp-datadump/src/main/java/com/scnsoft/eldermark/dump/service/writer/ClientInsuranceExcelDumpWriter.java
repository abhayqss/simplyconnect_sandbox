package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.ClientInsuranceDump;
import com.scnsoft.eldermark.dump.model.ClientInsuranceInfo;
import com.scnsoft.eldermark.dump.model.DumpType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ClientInsuranceExcelDumpWriter extends ExcelDumpWriter implements DumpWriter<ClientInsuranceDump> {

    @Override
    public DumpType getDumpType() {
        return DumpType.CLIENT_INSURANCE;
    }

    @Override
    public void writeDump(ClientInsuranceDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    Workbook generateWorkBook(ClientInsuranceDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);

        addClientInsuranceInfo(workbook, styles, dump.getClientInsuranceInfoList());
        return workbook;
    }

    private void addClientInsuranceInfo(XSSFWorkbook wb, Map<String, CellStyle> styles,
                                        List<ClientInsuranceInfo> clientInsuranceInfoList) {
        var sheet = wb.createSheet("Clients");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList(
                "Client name",
                "Community",
                "Status",
                "Insurance network",
                "Insurance plan"
        );

        writeHeader(headerRow, styles, headerValues);

        for (var value : clientInsuranceInfoList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getFirstName() + " " + value.getLastName());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.isActive() ? "Active" : "Inactive");
            writeToCell(styles, row.createCell(colCount++), value.getInsuranceNetwork());
            writeToCell(styles, row.createCell(colCount++), value.getInsurancePlan());
        }

        autosizeWidth(sheet, headerValues.size());
    }
}
