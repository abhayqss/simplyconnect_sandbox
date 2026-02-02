package com.scnsoft.eldermark.service.report.workbook;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.autosizeWidth;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.createStyles;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeBooleanYesNoToCell;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCell;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCellEmptyIfNa;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeWrappedTextToCell;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.EventsNotesReport;
import com.scnsoft.eldermark.service.report.generator.EventsNotesReportGenerator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class EventsNotesWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<EventsNotesReport> {

    private static final Logger logger = LoggerFactory.getLogger(EventsNotesReportGenerator.class);

    private final static List<String> EVENTS_REPORT_HEADERS = List.of(
        "Organization Name",
        "Community Name",
        "Client ID",
        "Client Name",
        "Date",
        "Event Type",
        "Submitted By",
        "Emergency Department Visit",
        "Overnight In-patient",
        "Location",
        "Situation",
        "Background",
        "Assessment",
        "Injury",
        "Follow up expected",
        "Follow up"
    );

    private final static List<String> NOTES_REPORT_HEADERS = List.of(
        "Organization Name",
        "Community Name",
        "Client ID",
        "Client Name",
        "Date",
        "Submitted By",
        "Note Type",
        "Encounter type",
        "Units",
        "Person completing the encounter",
        "Encounter Date",
        "Subjective",
        "Objective",
        "Assessment",
        "Plan"
    );
    public static final String EVENTS_SHEET_NAME = "Events";
    public static final String NOTES_SHEET_NAME = "Notes";

    @Override
    public Workbook generateWorkbook(EventsNotesReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addEventsTab(workbook, styles, report);
        addNotesTab(workbook, styles, report);
        logger.debug("Finished generating EventsNotesReport report");
        return workbook;
    }

    private void addNotesTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, EventsNotesReport report) {
        var headerValues = NOTES_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, NOTES_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getNotesRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            writeToCell(styles, row.createCell(colCount++), itRow.getOrganizationName());
            for (var communityRow : itRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount);
                    var clientColCount = communityColCount;
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientId());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientName());
                    for (var noteRow : clientRow.getSingleNoteRows()) {
                        row = getOrCreateRow(sheet, rowCount++);
                        var noteColCount = clientColCount;
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++),
                            formatToDate(noteRow.getDate(), report.getTimeZoneOffset()));
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++), noteRow.getSubmittedBy());
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++), noteRow.getNoteType());
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++), noteRow.getEncounterType());
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++), noteRow.getUnits());
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++), noteRow.getPersonCompletingEncounter());
                        writeToCellEmptyIfNa(styles, row.createCell(noteColCount++),
                            formatToDate(noteRow.getEncounterDate(), report.getTimeZoneOffset())
                        );
                        writeWrappedTextToCell(styles, row.createCell(noteColCount++), noteRow.getSubjective());
                        writeWrappedTextToCell(styles, row.createCell(noteColCount++), noteRow.getObjective());
                        writeWrappedTextToCell(styles, row.createCell(noteColCount++), noteRow.getAssessment());
                        writeWrappedTextToCell(styles, row.createCell(noteColCount++), noteRow.getPlan());
                    }
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addEventsTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, EventsNotesReport report) {
        var headerValues = EVENTS_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, EVENTS_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getEventRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            writeToCell(styles, row.createCell(colCount++), itRow.getOrganizationName());
            for (var communityRow : itRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount);
                    var clientColCount = communityColCount;
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientId());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientName());
                    for (var eventRow : clientRow.getSingleEventRows()) {
                        row = getOrCreateRow(sheet, rowCount++);
                        var eventColCount = clientColCount;
                        writeToCellEmptyIfNa(styles, row.createCell(eventColCount++),
                            formatToDate(eventRow.getDate(), report.getTimeZoneOffset()));
                        writeToCellEmptyIfNa(styles, row.createCell(eventColCount++), eventRow.getEventType());
                        writeToCellEmptyIfNa(styles, row.createCell(eventColCount++), eventRow.getSubmittedBy());
                        writeBooleanYesNoToCell(row.createCell(eventColCount++), eventRow.getEmergencyDepartmentVisit());
                        writeBooleanYesNoToCell(row.createCell(eventColCount++), eventRow.getOverNightInPatient());
                        writeWrappedTextToCell(styles, row.createCell(eventColCount++), eventRow.getLocation());
                        writeWrappedTextToCell(styles, row.createCell(eventColCount++), eventRow.getSituation());
                        writeWrappedTextToCell(styles, row.createCell(eventColCount++), eventRow.getBackground());
                        writeWrappedTextToCell(styles, row.createCell(eventColCount++), eventRow.getAssessment());
                        writeBooleanYesNoToCell(row.createCell(eventColCount++), eventRow.getHasInjury());
                        writeBooleanYesNoToCell(row.createCell(eventColCount++), eventRow.getFollowUpExpected());
                        if (eventRow.getFollowUpExpected()){
                            writeWrappedTextToCell(styles, row.createCell(eventColCount++), eventRow.getFollowUp());
                        }
                    }
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.EVENTS_NOTES;
    }
}
