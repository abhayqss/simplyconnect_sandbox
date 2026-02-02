package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.cstracking.CsTrackingReport;
import com.scnsoft.eldermark.beans.reports.model.cstracking.CsTrackingReportInsuranceAuthorizationDto;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class CsTrackingWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<CsTrackingReport> {

    private static final Logger logger = LoggerFactory.getLogger(CsTrackingWorkbookGenerator.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final static List<String> CS_TRACKING_REPORT_HEADERS = List.of(
            "Provider Name",
            "Project Name",
            "Care Coordinator Name",
            "Client ID",
            "Client Status",
            "Client First Name",
            "Client Last Name",
            "Date of Birth",
            "Medicaid #",
            "Phone Number",
            "Housing Status",
            "Authorization Start Date",
            "Authorization End Date",
            "Authorization Number",
            "CS First Face to Face Encounter Date",
            "Disenrollment Date",
            "Reason for Disenrollment",
            "Last Arizona Matrix(AZ) was completed",
            "Total number of AZ that have been completed",
            "Date last HMIS was completed",
            "Total number of HMIS that have been completed",
            "Date last Nor Cal was completed",
            "Total number of Nor Cals that have been completed",
            "Date last Housing Assessment was completed",
            "Total number of Housing Assessments completed",
            "Date of last Case Note",
            "Total number of Case Notes completed",
            "Date of last Service Plan",
            "Total number of Service Plans completed",
            "Date of last Event",
            "Total number of Event Notes completed"
    );

    public static final String CS_TRACKING_SHEET_NAME = "CS Tracking";

    @Override
    public Workbook generateWorkbook(CsTrackingReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addCsTrackingTab(workbook, styles, report);
        logger.debug("Finished generating CsTracking report");
        return workbook;
    }

    private void addCsTrackingTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, CsTrackingReport report) {
        var headerValues = CS_TRACKING_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, CS_TRACKING_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            colCount++;
            for (var communityRow : itRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = communityColCount;
                    writeToCellEmptyIfNa(styles, row.createCell(0), clientRow.getInsuranceName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientCareCoordinatorName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientId());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientStatus());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientFirstName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientLastName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDate(clientRow.getDateOfBirth(), DATE_FORMAT));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMedicaid());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getPhoneNumber());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getHousingStatus());

                    writeWrappedTextToCellWithDateListWithComma(styles, row.createCell(clientColCount++), clientRow.getInsuranceAuthorizations().stream()
                            .map(CsTrackingReportInsuranceAuthorizationDto::getStartDate)
                            .collect(Collectors.toList()), report.getTimeZoneOffset());
                    writeWrappedTextToCellWithDateListWithComma(styles, row.createCell(clientColCount++), clientRow.getInsuranceAuthorizations().stream()
                            .map(CsTrackingReportInsuranceAuthorizationDto::getEndDate)
                            .collect(Collectors.toList()), report.getTimeZoneOffset());
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getInsuranceAuthorizations().stream()
                            .map(CsTrackingReportInsuranceAuthorizationDto::getNumber)
                            .collect(Collectors.toList()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getCsFaceToFaceFirstEncounterDate(), report.getTimeZoneOffset()));

                    writeWrappedTextToCellWithDateListWithComma(styles, row.createCell(clientColCount++), clientRow.getDisenrollmentDates(), report.getTimeZoneOffset());
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getDisenrollmentReasons().stream()
                            .map(reason -> reason != null ? reason.getTitle() : null)
                            .collect(Collectors.toList()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastArizonaAssessmentCompleteDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberCompletedArizonaAssessment());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastHmisAssessmentCompleteDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberCompletedHmisAssessment());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastNorCalAssessmentCompleteDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberCompletedNorCalAssessment());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastHousingAssessmentCompleteDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberCompletedHousingAssessment());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastCaseNoteDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberCaseNotes());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastServicePlanDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberServicePlansCompleted());

                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getLastEventDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTotalNumberEventNotesCompleted());
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.CS_TRACKING;
    }
}