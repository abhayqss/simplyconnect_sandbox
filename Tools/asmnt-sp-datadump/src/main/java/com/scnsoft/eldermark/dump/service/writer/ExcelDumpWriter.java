package com.scnsoft.eldermark.dump.service.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;


public abstract class ExcelDumpWriter {

    static final int CHAR_LENGTH = 256;
    static final int MAX_COL_WIDTH_IN_CHAR = 255;
    static final String NA = "n/a";


    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm a");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    void writeToConsole(Dump dump) {
        try {
            var result = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dump);

            System.out.println();
            System.out.println(result);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sheet createSheetWithHeader(Workbook wb, Map<String, CellStyle> styles, String sheetName, List<String> headerValues, int startRowNumber) {
        var sheet = wb.createSheet(sheetName);

        if (startRowNumber > 0) {
            for (int i = 0; i < startRowNumber; i++) {
                Row row = sheet.createRow(i);
                row.setZeroHeight(true);
            }
            Row headerRow = sheet.createRow(startRowNumber++);
            //TODO "2" looks like unknown magic number
            writeHeader(headerRow, styles, headerValues, 2);
        } else {
            Row headerRow = sheet.createRow(startRowNumber++);
            writeHeader(headerRow, styles, headerValues);
        }

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);
        return sheet;
    }

    void addGad7PHQ9Scoring(Workbook wb, Map<String, CellStyle> styles, List<GAD7PHQ9Scoring> gad7PHQ9ScoringList) {
        var sheet = wb.createSheet("GAD-7 & PHQ-9 scoring");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("ID", "First Name*", "Last Name*", "Organization*", "Community*", "GAD-7 score", "PHQ-9 score");

        writeHeader(headerRow, styles, headerValues);

        for (var value : gad7PHQ9ScoringList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getResidentId());
            writeToCell(styles, row.createCell(colCount++), value.getFirstName());
            writeToCell(styles, row.createCell(colCount++), value.getLastName());
            writeToCell(styles, row.createCell(colCount++), value.getOrganization());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            if (value.getGad7scores().size() == 1) {
                writeToCell(styles, row.createCell(colCount++), value.getGad7scores().get(0)); //store as number
            } else {
                withNa(row.createCell(colCount++), styles.get("right_align"), StringUtils.join(value.getGad7scores(), ", "));
            }

            if (value.getPhq9Scores().size() == 1) {
                writeToCell(styles, row.createCell(colCount++), value.getPhq9Scores().get(0)); //store as number
            } else {
                withNa(row.createCell(colCount++), styles.get("right_align"), StringUtils.join(value.getPhq9Scores(), ", "));
            }

            if (!value.isActive()) {
                writeToCell(styles, row.createCell(colCount++), "inactive patient");
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }


    void addComprehensiveDetails(XSSFWorkbook wb, Map<String, CellStyle> styles, List<ComprehensiveDetail> comprehensiveDetailList) {
        var sheet = wb.createSheet("Comprehensive Details");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Patient name", "Community", "Time to complete (mins)", "Gender (record)", "Gender (assessment)",
                "Race (record)", "Race (assessment)", "Age", "Insurance Network", "Insurance Plan");

        writeHeader(headerRow, styles, headerValues);

        for (var value : comprehensiveDetailList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getPatientName());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.getTimeToCompleteInMinutes());
            writeToCell(styles, row.createCell(colCount++), value.getGenderFromClient());
            writeToCell(styles, row.createCell(colCount++), value.getGenderFromAssessment());
            writeToCell(styles, row.createCell(colCount++), value.getRaceFromClient());
            writeToCell(styles, row.createCell(colCount++), value.getRaceFromAssessment());
            writeToCell(styles, row.createCell(colCount++), value.getAge());
            writeToCell(styles, row.createCell(colCount++), value.getInsuranceNetwork());
            writeToCell(styles, row.createCell(colCount++), value.getInsurancePlan());
        }

        autosizeWidth(sheet, headerValues.size());
    }

    void addSpIndividuals(XSSFWorkbook wb, Map<String, CellStyle> styles, List<SPIndividual> spIndividualList) {
        var sheet = wb.createSheet("SP - Individuals");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);


        var headerValues = Arrays.asList("Resource Name", "Score of tied to Service Plan need", "Domain", "Patient", "Community");
        writeHeader(headerRow, styles, headerValues);

        for (var value : spIndividualList) {
            var row = sheet.createRow(rowCount++);

            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getResourceName());
            writeToCell(styles, row.createCell(colCount++), value.getScoreOfTiedToServicePlanNeed());
            writeToCell(styles, row.createCell(colCount++), value.getDomain());
            writeToCell(styles, row.createCell(colCount++), value.getPatient());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());

        }

        autosizeWidth(sheet, headerValues.size());
    }


    void addSpDetails(Workbook wb, Map<String, CellStyle> styles, List<SPDetails> spDetailsList) {
        var sheet = wb.createSheet("SP - Details");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Patient name", "Community", "Service Plan Status", "Number of days to complete", "Events count (ER, Hospitalization)", "Goals count");

        writeHeader(headerRow, styles, headerValues);

        for (var value : spDetailsList) {
            var row = sheet.createRow(rowCount++);

            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getPatientName());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.getStatus());
            writeToCell(styles, row.createCell(colCount++), value.getDaysToComplete());
            writeToCell(styles, row.createCell(colCount++), value.getEventsCount());
            writeToCell(styles, row.createCell(colCount++), value.getGoalsCount());

        }


        autosizeWidth(sheet, headerValues.size());
    }

    protected String formatToDate(Instant temporal, ZoneId zoneId) {
        return format(temporal, zoneId, DATE_FORMAT);
    }

    protected String formatToDate(TemporalAccessor temporal) {
        return ofNullable(temporal)
                .map(i -> DATE_FORMAT.format(temporal))
                .orElse(null);
    }

    protected String formatToTime(Instant temporal, ZoneId zoneId) {
        return format(temporal, zoneId, TIME_FORMAT);
    }

    protected String formatToDateTime(Instant temporal, ZoneId zoneId) {
        return format(temporal, zoneId, DATE_TIME_FORMAT);
    }

    protected String format(TemporalAccessor temporal, ZoneId zoneId, DateTimeFormatter dateTimeFormatter) {
        return ofNullable(temporal)
                .map(i -> dateTimeFormatter.withZone(zoneId).format(temporal))
                .orElse(null);
    }

    void writeToCell(Map<String, CellStyle> styles, Cell cell, String value) {
        cell.setCellValue(StringUtils.firstNonEmpty(value, NA));
    }

    void writeToCellOrEmpty(Map<String, CellStyle> styles, Cell cell, String value) {
        cell.setCellValue(StringUtils.defaultString(value));
    }

//    void writeToCell(Map<String, CellStyle> styles, Cell cell, Instant value) {
//        if (value == null) {
//            cell.setCellValue(NA);
//            cell.setCellStyle(styles.get("right_align"));
//        } else {
//            cell.setCellValue(new Calendar.Builder().setInstant(value.toEpochMilli()).build());
//        }
//    }
//
//    void writeToCell(Map<String, CellStyle> styles, Cell cell, LocalDate value) {
//        if (value == null) {
//            cell.setCellValue(NA);
//            cell.setCellStyle(styles.get("right_align"));
//        } else {
//            var c = new Calendar.Builder()
//                    .setDate(value.getYear(), value.getMonth().getValue() - 1, value.getDayOfYear())
//                    .build();
//            cell.setCellValue(c);
//        }
//    }

    void writeFormulaToCell(Map<String, CellStyle> styles, Cell cell, String value, Integer errorValue) {
        cell.setCellFormula(String.format("IFERROR(%s, %d)", value, errorValue));
    }

    void writeToCell(Map<String, CellStyle> styles, Cell cell, Number value) {
        if (value == null) {
            cell.setCellValue(NA);
            cell.setCellStyle(styles.get("right_align"));
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }


    void writeHeader(Row headerRow, Map<String, CellStyle> styles, List<String> headerValues) {
        writeHeader(headerRow, styles, headerValues, 0);
    }

    public static void writeHeader(Row headerRow, Map<String, CellStyle> styles, List<String> headerValues, int colCount) {
        for (int i = colCount; i < headerValues.size() + colCount; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerValues.get(i - colCount));
            cell.setCellStyle(styles.get("header"));
        }
    }

    void autosizeWidth(Sheet sheet, int size) {
        for (int i = 0; i < size; i++) {
            sheet.autoSizeColumn(i, true);
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + CHAR_LENGTH * 3, CHAR_LENGTH * MAX_COL_WIDTH_IN_CHAR));
        }
    }


    void writeGeneralRow(Sheet sheet, int idx, Map<String, CellStyle> styles, String title, double value) {
        var row = sheet.createRow(idx);
        var cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(styles.get("bold"));

        cell = row.createCell(1);
        cell.setCellValue(value);

    }

    void writeGeneralRowFormula(Sheet sheet, int idx, Map<String, CellStyle> styles, String title, String formula) {
        var row = sheet.createRow(idx);
        var cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(styles.get("bold"));

        cell = row.createCell(1);
        cell.setCellFormula(formula);
    }

    Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();

        CellStyle style;
        Font boldFont = wb.createFont();
        boldFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(boldFont);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setFont(boldFont);
        styles.put("bold", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("right_align", style);

        return styles;
    }

    void withNa(Cell cell, CellStyle cellStyle, String value) {
        cell.setCellValue(StringUtils.firstNonEmpty(value, NA));
        cell.setCellStyle(cellStyle);
    }


    void writeToFile(Workbook workbook, Dump dump) {
        StringBuilder file = new StringBuilder("dump_")
                .append(dump.getDumpType());
        if (StringUtils.isNotEmpty(dump.getMetaInformation())) {
            file.append("_").append(dump.getMetaInformation());
        }
        file.append(".xlsx");
        try {
            FileOutputStream out = new FileOutputStream(file.toString());
            workbook.write(out);
            out.close();
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
