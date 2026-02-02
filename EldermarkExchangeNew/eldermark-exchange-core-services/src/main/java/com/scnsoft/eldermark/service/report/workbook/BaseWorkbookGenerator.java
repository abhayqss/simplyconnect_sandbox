package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.model.*;
import com.scnsoft.eldermark.service.report.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;
import static com.scnsoft.eldermark.util.DateTimeUtils.generateZoneOffset;
import static com.scnsoft.eldermark.util.DateTimeUtils.toLocalDateTimeAtZone;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public abstract class BaseWorkbookGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm a");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a");
    private static final DateTimeFormatter DATE_TIME_FORMAT_12H = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    private static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("MM-yyyy");

    @Autowired
    protected ReportService reportService;

    public void addReportingCriteriaTab(Workbook wb, Map<String, CellStyle> styles, Report report) {
        var sheet = wb.createSheet("Reporting criteria");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        List<String> headerValues = new LinkedList<>(Arrays.asList("Community", "Report type", "From"));
        if (nonNull(report.getDateTo())) {
            headerValues.add("To");
        }

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        List<String> communityNames = report.getCommunityNames();
        for (var communityName : communityNames) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), communityName);
            if (rowCount == 2) {
                writeToCell(styles, row.createCell(colCount++), reportService.findConfigurationByType(report.getReportType()).getDisplayName());
                writeToCell(styles, row.createCell(colCount++), formatToDate(report.getDateFrom(), report.getTimeZoneOffset()));
                if (nonNull(report.getDateTo())) {
                    writeToCell(styles, row.createCell(colCount++), formatToDate(report.getDateTo(), report.getTimeZoneOffset()));
                }
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    protected String formatToDate(Instant instant, Integer timeZoneOffset) {
        return format(instant, timeZoneOffset, DATE_FORMAT);
    }

    protected String formatToDate(LocalDate date, DateTimeFormatter formatter) {
        return ofNullable(date).map(formatter::format).orElse(null);
    }

    protected String formatToDate(LocalDate date) {
        return formatToDate(date, DATE_FORMAT);
    }

    protected String formatToDate(Instant instant, ZoneId zoneId, DateTimeFormatter formatter) {
        return format(instant, zoneId, formatter);
    }

    protected String formatToTime(Instant instant, Integer timeZoneOffset) {
        return format(instant, timeZoneOffset, TIME_FORMAT);
    }

    protected String formatToDateTime(Instant instant, Integer timeZoneOffset) {
        return format(instant, timeZoneOffset, DATE_TIME_FORMAT);
    }

    protected String formatToDateTime12h(Instant instant, Integer timeZoneOffset) {
        return format(instant, timeZoneOffset, DATE_TIME_FORMAT_12H);
    }

    protected String formatToMonthYear(Instant instant, Integer timeZoneOffset) {
        return format(instant, timeZoneOffset, MONTH_YEAR_FORMAT);
    }

    private String format(Instant instant, Integer timeZoneOffset, DateTimeFormatter dateTimeFormatter) {
        return format(instant, generateZoneOffset(timeZoneOffset), dateTimeFormatter);
    }

    private String format(Instant instant, ZoneId zoneId, DateTimeFormatter dateTimeFormatter) {
        return ofNullable(instant)
                .map(i -> dateTimeFormatter.withZone(zoneId).format(instant))
                .orElse(null);
    }

    protected String formatTime(LocalDateTime dateTime, Integer timeZoneOffset) {
        return ofNullable(dateTime)
                .map(dt -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm a");
                    return toLocalDateTimeAtZone(dateTime, timeZoneOffset).format(formatter);
                })
                .orElse("");
    }

    protected void addEncNoteFirstTab(Sheet sheet, List<String> headerValues, Map<String, CellStyle> styles, List<EncounterNoteFirstTab> firstTabList) {
        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        for (var firstTabRow : firstTabList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), firstTabRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), firstTabRow.getClientNames());
            writeToCell(styles, row.createCell(colCount++), firstTabRow.getClientIds());
            writeToCell(styles, row.createCell(colCount++), firstTabRow.getTotalTimeSpent());
            writeToCell(styles, row.createCell(colCount++), firstTabRow.getServiceCoordinatorName());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    protected void addEncNoteSecondTab(Sheet sheet, List<String> headerValues, Map<String, CellStyle> styles, List<EncounterNoteSecondTab> secondTabList, Integer timeZoneOffset) {
        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        for (var secondTabRow : secondTabList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), secondTabRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getClientNames());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getClientIds());
            writeToCell(styles, row.createCell(colCount++), formatToDateTime(secondTabRow.getFromTime(), timeZoneOffset) + " - " + formatToTime(secondTabRow.getToTime(), timeZoneOffset));
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getTimeSpent());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getServiceCoordinatorName());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getSubjective());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getObjective());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getAssessment());
            writeToCell(styles, row.createCell(colCount++), secondTabRow.getPlan());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
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

    public Sheet createSheetWithHeaderAlignLeft(Workbook wb, Map<String, CellStyle> styles, String sheetName, List<String> headerValues) {
        var sheet = wb.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        writeHeaderAlignLeft(headerRow, styles, headerValues);
        return sheet;
    }

    protected void addTotalClientsTab(Workbook wb, Map<String, CellStyle> styles, Map<String, List<TotalClientsTab>> totalClientsMap) {

        var sheet = wb.createSheet("Total, clients");
        var headerValues = asList("Community Name", "Resident/Client Name", "Resident/Client ID", "In person time with individuals, total (minutes)", "Phone call time with individuals or coordinating services, total (minutes)", "Total, minutes");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        for (var entry : totalClientsMap.entrySet()) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), entry.getKey());
            var scCount = 0;
            for (var totalClient : entry.getValue()) {
                if (scCount > 0) {
                    row = sheet.createRow(rowCount++);
                }
                colCount = 1;
                writeToCell(styles, row.createCell(colCount++), totalClient.getClientName());
                writeToCell(styles, row.createCell(colCount++), totalClient.getClientId());
                writeToCell(styles, row.createCell(colCount++), totalClient.getInPersonTimeWithIndividualsTotalMin());
                writeToCell(styles, row.createCell(colCount++), totalClient.getPhoneCallTimeWithIndividualsOrServicesTotalMin());
                writeToCell(styles, row.createCell(colCount++), totalClient.getTotalMinutes());
                scCount++;
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    protected void addTotalServiceCoordinatorsTab(Workbook wb, Map<String, CellStyle> styles, Map<String, List<TotalServiceCoordinatorsTab>> totalSCMap) {

        var sheet = wb.createSheet("Total, service coordinators");
        var headerValues = asList("Community Name", "Service Coordinator Name", "In person time with individuals, total (minutes)", "Phone call time with individuals or coordinating services, total (minutes)", "Total, minutes");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        for (var entry : totalSCMap.entrySet()) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), entry.getKey());

            var scCount = 0;
            for (var totalSC : entry.getValue()) {
                if (scCount > 0) {
                    row = sheet.createRow(rowCount++);
                }
                colCount = 1;
                writeToCell(styles, row.createCell(colCount++), totalSC.getServiceCoordinatorName());
                writeToCell(styles, row.createCell(colCount++), totalSC.getInPersonTimeWithIndividualsTotalMin());
                writeToCell(styles, row.createCell(colCount++), totalSC.getPhoneCallTimeWithIndividualsTotalMin());
                writeToCell(styles, row.createCell(colCount++), totalSC.getTotalMin());
                scCount++;
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    public Row getOrCreateRow(Sheet sheet, int rowCount) {
        var row = sheet.getRow(rowCount);
        return row != null ? row : sheet.createRow(rowCount);
    }

    public void writeToCellWithDateList(Map<String, CellStyle> styles, Cell cell, Collection<Instant> dates, Integer timeZoneOffset) {
        cell.setCellStyle(styles.get("date_list"));

        var dateList = Optional.ofNullable(dates)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .map(date -> formatToDate(date, timeZoneOffset))
                .collect(Collectors.joining("\n"));

        writeToCell(styles, cell, dateList);
    }

    public void writeToCellWithDateTimeList(Map<String, CellStyle> styles, Cell cell, Collection<Instant> dates, Integer timeZoneOffset) {
        cell.setCellStyle(styles.get("date_list"));

        var dateList = Optional.ofNullable(dates)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .map(date -> formatToDateTime(date, timeZoneOffset))
                .collect(Collectors.joining("\n"));

        writeToCell(styles, cell, dateList);
    }

    public void writeToCellWithNullableDateTimeList(Map<String, CellStyle> styles, Cell cell, Collection<Instant> dates, Integer timeZoneOffset) {
        cell.setCellStyle(styles.get("date_list"));

        var dateList = Optional.ofNullable(dates)
                .orElseGet(ArrayList::new)
                .stream()
                .map(date -> date != null
                        ? formatToDateTime(date, timeZoneOffset)
                        : StringUtils.EMPTY)
                .collect(Collectors.joining("\n"));

        writeToCell(styles, cell, dateList);
    }

    public void writeToCellWithNullableDateList(Map<String, CellStyle> styles, Cell cell, Collection<Instant> dates, Integer timeZoneOffset) {
        cell.setCellStyle(styles.get("date_list"));

        var dateList = Optional.ofNullable(dates)
                .orElseGet(ArrayList::new)
                .stream()
                .map(date -> date != null
                        ? formatToDate(date, timeZoneOffset)
                        : StringUtils.EMPTY)
                .collect(Collectors.joining("\n"));

        writeToCell(styles, cell, dateList);
    }

    public void writeWrappedTextToCellWithDateListWithComma(Map<String, CellStyle> styles, Cell cell, List<Instant> dates, Integer timeZoneOffset) {
        cell.setCellStyle(styles.get("wrapped_text"));

        var dateList = Optional.ofNullable(dates)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .map(date -> formatToDateTime(date, timeZoneOffset))
                .collect(Collectors.joining(", "));

        writeToCellEmptyIfNa(styles, cell, dateList);
    }

    public void writeWrappedTextToCellWithStringCollectionWithComma(Map<String, CellStyle> styles, Cell cell, Collection<String> strings) {
        cell.setCellStyle(styles.get("wrapped_text"));

        var stringList = Optional.ofNullable(strings)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        writeToCellEmptyIfNa(styles, cell, stringList);
    }

    public void writeWrappedTextToCellWithStringCollectionWithCommaNaIfEmptyItem(Map<String, CellStyle> styles, Cell cell, Collection<String> strings) {
        cell.setCellStyle(styles.get("wrapped_text"));

        var stringList = Optional.ofNullable(strings)
                .orElseGet(ArrayList::new)
                .stream()
                .map(str-> StringUtils.firstNonEmpty(str, NA))
                .collect(Collectors.joining(", "));

        writeToCellEmptyIfNa(styles, cell, stringList);
    }
}
