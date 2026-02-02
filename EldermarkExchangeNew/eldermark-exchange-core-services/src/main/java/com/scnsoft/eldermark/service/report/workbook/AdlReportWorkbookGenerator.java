package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.AdlReportField;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.AdlReport;
import com.scnsoft.eldermark.beans.reports.model.AdlReportMedicalHistoryRow;
import com.scnsoft.eldermark.beans.reports.model.AdlReportRow;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.scnsoft.eldermark.beans.reports.enums.AdlReportField.*;
import static com.scnsoft.eldermark.beans.reports.enums.ReportType.ADL_REPORT;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Component
public class AdlReportWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<AdlReport> {

    private static final List<AdlReportField> MEDICAL_HISTORY_FIELDS = List
            .of(MEDICAL_HISTORY_CARDIAC, MEDICAL_HISTORY_PULMONARY, MEDICAL_HISTORY_DIABETIC, MEDICAL_HISTORY_NEUROLOGICAL,
                    MEDICAL_HISTORY_GASTROINTESTINA, MEDICAL_HISTORY_MUSCULOSKELETAL, MEDICAL_HISTORY_GYN_URINARY, MEDICAL_HISTORY_INFECTIOUS_DISEASE, MEDICAL_HISTORY_IMMUNE_DISORDERS,
                    MEDICAL_HISTORY_BEHAVIORAL_HEALTH, MEDICAL_HISTORY_WOUNDS, MEDICAL_HISTORY_VISION_HEARING_DENTAL);


    @Override
    public Workbook generateWorkbook(AdlReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);


        addReportingCriteriaTab(workbook, styles, report);
        addADLsSheet(workbook, styles, report.getAdlRows());
        addIADLsSheet(workbook, styles, report.getIadlRows());
        addMedicalProblemsSheet(workbook, styles, report.getMedicalHistoryRows());
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ADL_REPORT;
    }

    private void addADLsSheet(Workbook workbook, Map<String, CellStyle> styles, List<AdlReportRow> adlRows) {
        String sheetName = "ADLs";
        var headerValues = Arrays.asList("Community name", "Resident/Client ID", "Resident/Client name", "ADL", "Assistance");
        Sheet sheet = createSheetWithHeaderAlignLeft(workbook, styles, sheetName, headerValues);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        for (var adlRow : Optional.ofNullable(adlRows).orElse(Collections.emptyList())) {
            for (var adlReport : adlRow.getFieldsWithContent().entrySet()) {
                var row = sheet.createRow(rowCount++);
                var colCount = 0;
                writeToCell(styles, row.createCell(colCount++), adlRow.getCommunityName());
                writeToCell(styles, row.createCell(colCount++), adlRow.getClientId());
                writeToCell(styles, row.createCell(colCount++), adlRow.getClientName());
                writeToCell(styles, row.createCell(colCount++), adlReport.getKey().getDisplayName());
                writeToCell(styles, row.createCell(colCount++), adlReport.getValue());
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addIADLsSheet(Workbook workbook, Map<String, CellStyle> styles, List<AdlReportRow> adlRows) {
        String sheetName = "IADLs";
        var headerValues = Arrays.asList("Community name", "Resident/Client ID", "Resident/Client name", "IADL", "Assistance");
        Sheet sheet = createSheetWithHeaderAlignLeft(workbook, styles, sheetName, headerValues);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        for (var adlRow : Optional.ofNullable(adlRows).orElse(Collections.emptyList())) {
            for (var adlReport : adlRow.getFieldsWithContent().entrySet()) {
                var row = sheet.createRow(rowCount++);
                var colCount = 0;
                writeToCell(styles, row.createCell(colCount++), adlRow.getCommunityName());
                writeToCell(styles, row.createCell(colCount++), adlRow.getClientId());
                writeToCell(styles, row.createCell(colCount++), adlRow.getClientName());
                writeToCell(styles, row.createCell(colCount++), adlReport.getKey().getDisplayName());
                writeToCell(styles, row.createCell(colCount++), adlReport.getValue());
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addMedicalProblemsSheet(Workbook workbook, Map<String, CellStyle> styles, List<AdlReportMedicalHistoryRow> medicalHistoryRows) {
        var sheetName = "Medical Problems";
        var headerValues = Arrays
                .asList("Community name", "Resident/Client name", "Cardiac", "Pulmonary", "Diabetic", "Neurological", "Gastrointestina", "Musculoskeletal",
                        "Gyn/urinary", "Infectious disease", "Immune disorders", "Chronic Pain", "Behavioral health", "Wounds", "Vision & Hearing & Dental");
        Sheet sheet = createSheetWithHeaderAlignLeft(workbook, styles, sheetName, headerValues);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 3));

        var rowCount = 1;
        for (var adlRow : CollectionUtils.emptyIfNull(medicalHistoryRows)) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), adlRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), adlRow.getClientName());
            Map<AdlReportField, Cell> medicalHistoryCellsRow = createMedicalHistoryCellsRow(row);
            if (MapUtils.isNotEmpty(adlRow.getFieldsWithContent())) {
                for (var adlReport : adlRow.getFieldsWithContent().entrySet()) {
                    Cell cell = medicalHistoryCellsRow.get(adlReport.getKey());
                    cell.setCellValue(adlReport.getValue());
                    writeToCell(styles, medicalHistoryCellsRow.get(adlReport.getKey()), adlReport.getValue());
                }
            }
            if (MapUtils.isNotEmpty(adlRow.getChronicPainRows())) {
                CellStyle cs = workbook.createCellStyle();
                cs.setWrapText(true);
                Cell cell = medicalHistoryCellsRow.get(MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION);
                cell.setCellStyle(cs);

                StringBuilder stringBuilder = new StringBuilder();
                for (var adlReportChronicPain : Optional.of(adlRow.getChronicPainRows().entrySet()).orElse(Collections.emptySet())) {
                    stringBuilder.append(adlReportChronicPain.getKey().getDisplayName()).append(": ").append(adlReportChronicPain.getValue()).append("\n");
                }
                writeToCell(styles, cell, stringBuilder.toString());
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }


    private Map<AdlReportField, Cell> createMedicalHistoryCellsRow(Row row) {
        var colCount = 2;
        Map<AdlReportField, Cell> map = new EnumMap<>(AdlReportField.class);
        for (var field : MEDICAL_HISTORY_FIELDS) {
            map.put(field, row.createCell(colCount++));
            if (colCount == 11) {
                Cell cell = row.createCell(colCount++);
                map.put(MEDICAL_HISTORY_CHRONIC_PAIN_LOCATION, cell);
            }
        }
        return map;
    }
}
