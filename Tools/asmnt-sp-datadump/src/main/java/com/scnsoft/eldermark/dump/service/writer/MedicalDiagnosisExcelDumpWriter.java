package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.model.MedicalDiagnosisDump;
import com.scnsoft.eldermark.dump.model.MedicalDiagnosisField;
import com.scnsoft.eldermark.dump.model.MedicalDiagnosisInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.dump.model.MedicalDiagnosisField.*;

@Component
public class MedicalDiagnosisExcelDumpWriter extends ExcelDumpWriter implements DumpWriter<MedicalDiagnosisDump> {

    private static final List<MedicalDiagnosisField> MEDICAL_HISTORY_FIELDS = List
            .of(MEDICAL_HISTORY_CARDIAC, MEDICAL_HISTORY_PULMONARY, MEDICAL_HISTORY_DIABETIC, MEDICAL_HISTORY_NEUROLOGICAL,
                    MEDICAL_HISTORY_GASTROINTESTINA, MEDICAL_HISTORY_MUSCULOSKELETAL, MEDICAL_HISTORY_GYN_URINARY, MEDICAL_HISTORY_INFECTIOUS_DISEASE, MEDICAL_HISTORY_IMMUNE_DISORDERS,
                    MEDICAL_HISTORY_BEHAVIORAL_HEALTH, MEDICAL_HISTORY_WOUNDS, MEDICAL_HISTORY_VISION_HEARING_DENTAL);
    private static final List<MedicalDiagnosisField> MEDICAL_HISTORY_CHRONIC_PAIN_FIELDS = List.of(MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION, MEDICAL_HISTORY_CHRONIC_PAIN_AGITATORS,
            MEDICAL_HISTORY_CHRONIC_PAIN_SEVERITY, MEDICAL_HISTORY_CHRONIC_PAIN_LENGTH_OF_TIME, MEDICAL_HISTORY_CHRONIC_PAIN_RELIEVING_FACTORS, MEDICAL_HISTORY_CHRONIC_PAIN_COMMENT);


    @Override
    public DumpType getDumpType() {
        return DumpType.MEDICAL_DIAGNOSIS;
    }

    @Override
    public void writeDump(MedicalDiagnosisDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    private Workbook generateWorkBook(MedicalDiagnosisDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        var headers = new ArrayList<String>();
        headers.add("Resident/Client name");
        headers.addAll(MEDICAL_HISTORY_FIELDS.stream().map(MedicalDiagnosisField::getDisplayName).collect(Collectors.toList()));
        headers.add("Chronic Pain");
        dump.getMedicalDiagnosisByCommunityNames().forEach(medicalDiagnosisByCommunityName -> addMedicalDiagnosisSheet(workbook, styles, headers, medicalDiagnosisByCommunityName));
        return workbook;
    }

    private void addMedicalDiagnosisSheet(Workbook wb, Map<String, CellStyle> styles, List<String> headers, Pair<String, List<MedicalDiagnosisInfo>> medicalDiagnosisByCommunityName) {
        var rowCount = 0;

        var sheet = wb.createSheet(medicalDiagnosisByCommunityName.getFirst());
        var headerRow = sheet.createRow(rowCount++);
        writeHeader(headerRow, styles, headers);
        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        var medicalDiagnosisInfo = medicalDiagnosisByCommunityName.getSecond();
        for (var medicalDiagnosisByClient : medicalDiagnosisInfo) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(row.createCell(colCount++), medicalDiagnosisByClient.getClientName());
            for (var field : MEDICAL_HISTORY_FIELDS) {
                var value = medicalDiagnosisByClient.getMedicalDiagnosisFields().getOrDefault(field, "");
                writeToCell(row.createCell(colCount++), value);
                if (field == MEDICAL_HISTORY_IMMUNE_DISORDERS) {
                    var chronicPainValue = MEDICAL_HISTORY_CHRONIC_PAIN_FIELDS.stream()
                            .map(f -> {
                                var v = medicalDiagnosisByClient.getMedicalDiagnosisFields().getOrDefault(f, "");
                                if (StringUtils.isNotEmpty(v)) {
                                    return f.getDisplayName() + ": " + v;
                                }
                                return null;
                            })
                            .filter(StringUtils::isNotEmpty)
                            .collect(Collectors.joining("\n"));
                    var cellStyle = wb.createCellStyle();
                    cellStyle.setWrapText(true);
                    var cell = row.createCell(colCount++);
                    writeToCell(cell, chronicPainValue);
                }
            }
        }
        autosizeWidth(sheet, headers.size());
    }

    private void writeToCell(Cell cell, String value) {
        cell.setCellValue(value);
    }
}
