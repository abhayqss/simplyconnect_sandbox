package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SdohFieldDescriptor;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.basic.AuditableEntityService.logger;

public final class ExcelUtils {

    public static final int CHAR_LENGTH = 256;
    public static final String NA = "n/a";
    public static final String YES = "Yes";
    public static final String NO = "No";

    public static void writeToCell(Map<String, CellStyle> styles, Cell cell, String value) {
        cell.setCellValue(StringUtils.firstNonEmpty(value, NA));
    }

    public static void writeBooleanYesNoToCell(Cell cell, boolean value) {
        cell.setCellValue(value ? YES : NO);
    }

    public static void writeToCellEmptyIfNa(Map<String, CellStyle> styles, Cell cell, String value) {
        cell.setCellValue(StringUtils.isNotBlank(value) ? value : "");
    }

    public static void writeToCellEmptyIfNa(Map<String, CellStyle> styles, Cell cell, Instant date) {
        if (date != null) {
            cell.setCellValue(new Date(date.toEpochMilli()));
            //cell style data type is not set as 'date'. Set it separately.
        }
    }

    public static void writeToCellWithFormula(Cell cell, String formula) {
        cell.setCellFormula(formula);
    }

    public static void writeToCell(Map<String, CellStyle> styles, Cell cell, Number value) {
        writeToCellWithDefault(styles, cell, value, NA);
    }

    public static void writeToCellEmptyIfNa(Map<String, CellStyle> styles, Cell cell, Number value) {
        writeToCellWithDefault(styles, cell, value, "");
    }

    public static void writeHeader(Row headerRow, Map<String, CellStyle> styles, List<String> headerValues) {
        for (int i = 0; i < headerValues.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerValues.get(i));
            cell.setCellStyle(styles.get("header"));
        }
    }

    public static void writeHeaderAlignLeft(Row headerRow, Map<String, CellStyle> styles, List<String> headerValues) {
        for (int i = 0; i < headerValues.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerValues.get(i));
            cell.setCellStyle(styles.get("header_align_left"));
        }
    }

    public static void writeHeader(Row headerRow, Map<String, CellStyle> styles, List<String> headerValues, int colCount) {
        for (int i = colCount; i < headerValues.size() + colCount; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerValues.get(i - colCount));
            cell.setCellStyle(styles.get("header"));
        }
    }

    public static void autosizeWidth(Sheet sheet, int size) {
        for (int i = 0; i < size; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min((sheet.getColumnWidth(i) + CHAR_LENGTH * 3), 65280));
        }
    }

    public static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();

        CellStyle style;
        Font boldFont = wb.createFont();
        boldFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(boldFont);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(boldFont);
        styles.put("header_align_left", style);

        style = wb.createCellStyle();
        style.setFont(boldFont);
        styles.put("bold", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("right_align", style);

        style = wb.createCellStyle();
//        style.setFillBackgroundColor(IndexedColors.RED.getIndex());
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.put("red_background", style);

        style = wb.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        styles.put("date_list", style);

        style = wb.createCellStyle();
        style.setWrapText(true);
        styles.put("wrapped_text", style);

        return styles;
    }

    public static void withNa(Cell cell, CellStyle cellStyle, String value) {
        cell.setCellValue(StringUtils.firstNonEmpty(value, NA));
        cell.setCellStyle(cellStyle);
    }

    private static void writeToCellWithDefault(Map<String, CellStyle> styles, Cell cell, Number value, String emptyValue) {
        if (value == null) {
            cell.setCellValue(emptyValue);
            cell.setCellStyle(styles.get("right_align"));
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }

    public static void writeToCellHighlightMissing(Map<String, CellStyle> styles, Cell cell, String value, SdohFieldDescriptor fieldDescriptor) {
        writeToCellHighlightMissing(styles, cell, value, fieldDescriptor.isRequired(), fieldDescriptor.getLength());
    }

    public static void writeToCellHighlightMissing(Map<String, CellStyle> styles, Cell cell, String value,
                                                   boolean isRequired, Integer truncateTo) {
        if (StringUtils.isNotEmpty(value)) {
            cell.setCellValue(CareCoordinationUtils.truncate(value, truncateTo));
        } else if (isRequired) {
            cell.setCellStyle(styles.get("red_background"));
        }
    }

    public static void writeWrappedTextToCell(Map<String, CellStyle> styles, Cell cell, String wrappedText) {
        cell.setCellStyle(styles.get("wrapped_text"));
        writeToCellEmptyIfNa(styles, cell, wrappedText);
    }

    public static byte[] createExcel(Workbook wb) {
        try (
                var baos = new ByteArrayOutputStream()
        ) {
            wb.write(baos);

            return baos.toByteArray();
        } catch (IOException e) {
            logger.warn("Failed to create excel", e);
            throw new InternalServerException(InternalServerExceptionType.EXCEL_IO_ERROR, e);
        }
    }

    public static void writeWrappedListTextNewLineToCell(Map<String, CellStyle> styles, Cell cell, List<String> strings) {
        cell.setCellStyle(styles.get("wrapped_text"));
        var resultText = Optional.ofNullable(strings)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        writeToCellEmptyIfNa(styles, cell, resultText);
    }
}
