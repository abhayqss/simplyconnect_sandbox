package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.outreachtracking.OutreachReturnTrackerReport;
import com.scnsoft.eldermark.entity.client.report.CareTeamMemberOutreachReportItem;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class OutreachReturnTrackerWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<OutreachReturnTrackerReport> {

    private static final Logger logger = LoggerFactory.getLogger(OutreachReturnTrackerWorkbookGenerator.class);

    private final static List<String> OTF_TAB_HEADERS = List.of(
            "Primary Payer (MCP) Identifier",
            "MCP Name",
            "ECM Provider Name",
            "ECM Provider National Provider Identifier (NPI)",
            "ECM Provider Tax ID Number(TIN)",
            "Client Name",
            "Member Client Index Number",
            "Provider Type",
            "Date of Outreach Attempt",
            "Outreach Attempt Method",
            "Outreach Attempt Successful",
            "Time Spent Performing Outreach"
    );

    private final static List<String> RTF_TAB_HEADERS = List.of(
            "Primary Payer (MCP) Identifier",
            "MCP Name",
            "Member Client Index Number (CIN)",
            "Member Last Name",
            "Member First Name",
            "Member New Address Indicator",
            "Member Homelessness Indicator",
            "Member Residential Address",
            "Member Residential City",
            "Member Residential State",
            "Member Residential Zip",
            "Member New Phone Number Indicator",
            "Member Phone Number",
            "Member POF Indicator",
            "Member POF",
            "Member ECM Status",
            "Member Acuity Level",
            "ECM Enrollment Start Date",
            "ECM Enrollment End Date",
            "Date of Latest Care Plan Revision",
            "Date of Most Recent Care Conference",
            "Date Assessment Started",
            "Date of Most Recent Completed Assessment",
            "Date of Most Recent Encounter with Member",
            "ECM Lead Care Manager Name",
            "ECM Lead Care Manager Name Phone Number",
            "ECM Lead Care Manager Email",
            "Discontinuation Date",
            "Discontinuation Reason Code",
            "Discontinuation Reason",
            "In Person",
            "Telephonic/Video",
            "Member Information Return Transmission File Production Date",
            "Member Information File Reporting Period",
            "ECM Provider Name",
            "ECM Provider National Provider Identifier (NPI)",
            "ECM Provider Tax ID Number (TIN)",
            "ECM Provider Phone Number"
    );

    public static final String OTF_SHEET_NAME = "OTF";
    public static final String RTF_SHEET_NAME = "RTF";

    @Override
    public Workbook generateWorkbook(OutreachReturnTrackerReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addOtfTab(workbook, styles, report);
        addRtfTab(workbook, styles, report);
        logger.debug("Finished generating OutreachReturnTracking report");
        return workbook;
    }

    private void addRtfTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, OutreachReturnTrackerReport report) {
        var headerValues = RTF_TAB_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, RTF_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getOutReachReturnTrackerRtfRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            for (var communityRow : itRow.getCommunityRows()) {
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = colCount;
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getPrimaryPayerIdentifier());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMcpName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberClientIndexNumber());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberLastName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberFirstName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberNewAddressIndicator());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberHomelessnessIndicator());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberResidentialAddress());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberResidentialCity());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberResidentialState());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberResidentialZip());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberNewPhoneNumberIndicator());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberPhoneNumber());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberPofIndicator());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberPof());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberEcmStatus());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberAcuityLevel());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getEcmEnrollmentStartDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getEcmEnrollmentEndDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDate(clientRow.getLatestCarePlanRevisionDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getMostRecentCareConferenceDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDate(clientRow.getAssessmentStartedDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDate(clientRow.getMostRecentCompletedAssessmentDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getMostRecentEncounterWithMemberDate(), report.getTimeZoneOffset()));
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getCareTeamMemberOutreachReportItems().stream()
                            .map(item -> item.getFullName() + " " + item.getRoleName())
                            .collect(Collectors.toList()));
                    writeWrappedTextToCellWithStringCollectionWithCommaNaIfEmptyItem(styles, row.createCell(clientColCount++), clientRow.getCareTeamMemberOutreachReportItems().stream()
                            .map(CareTeamMemberOutreachReportItem::getPhoneNumber)
                            .collect(Collectors.toList()));
                    writeWrappedTextToCellWithStringCollectionWithCommaNaIfEmptyItem(styles, row.createCell(clientColCount++), clientRow.getCareTeamMemberOutreachReportItems().stream()
                            .map(CareTeamMemberOutreachReportItem::getEmail)
                            .collect(Collectors.toList()));
                    writeWrappedTextToCellWithDateListWithComma(styles, row.createCell(clientColCount++), clientRow.getDiscontinuationDates(), report.getTimeZoneOffset());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getDiscontinuationReasonCode());
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getDiscontinuationReasons().stream()
                            .map(reason -> reason != null ? reason.getTitle() : null)
                            .collect(Collectors.toList()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getInPerson());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getTelephonicVideo());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatToDate(clientRow.getMemberInformationReturnTransmissionFileProductionDate(), report.getTimeZoneOffset()));
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberInformationFileReportingPeriod());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderNationalProviderIdentifier());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderTaxIdNumber());
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getEcmProviderPhoneNumbers());
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private String formatCareTeamMembers(Map<String, List<String>> ecmLeadCareManagerName) {
        return Optional.ofNullable(ecmLeadCareManagerName)
                .map(Map::entrySet).stream()
                .flatMap(Collection::stream)
                .map(entry -> entry.getValue().stream().map(value -> value + " " + entry.getKey()).collect(Collectors.joining(", ")))
                .collect(Collectors.joining(", "));
    }

    private void addOtfTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, OutreachReturnTrackerReport report) {
        var headerValues = OTF_TAB_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, OTF_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getOutreachReturnTrackerOtfRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            for (var communityRow : itRow.getCommunityRows()) {
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = colCount;
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getPrimaryPayerIdentifier());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMcpName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderNationalProviderIdentifier());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getEcmProviderTaxIdNumber());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientName());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getMemberClientIndexNumber());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getProviderType());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatDateOfOutreachAttempts(clientRow.getDateOfOutreachAttempts(), report.getTimeZoneOffset()));
                    writeWrappedTextToCellWithStringCollectionWithComma(styles, row.createCell(clientColCount++), clientRow.getOutreachAttemptMethods());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getOutreachAttemptSuccessful());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), formatTimeSpentPerformingOutreach(clientRow.getTimeSpentPerformingOutreach()));
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private String formatTimeSpentPerformingOutreach(List<Long> times) {
        return Optional.ofNullable(times)
                .stream()
                .flatMap(Collection::stream)
                .reduce(Long::sum)
                .map(time -> {
                    if (time > 60) {
                        return (time / 60) + " h " + (time % 60) + " mins";
                    }
                    return time + " mins";
                })
                .orElse(null);
    }

    private String formatDateOfOutreachAttempts(List<Pair<Instant, Instant>> dates, Integer timeZoneOffset) {
        return Optional.ofNullable(dates)
                .stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.nullsLast(Comparator.comparing(Pair::getFirst)))
                .map(pair -> DateTimeUtils.formatDateTime(pair.getFirst(), timeZoneOffset) + " - " + DateTimeUtils.formatDateTime(pair.getSecond(), timeZoneOffset))
                .collect(Collectors.joining(", "));
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.OUTREACH_RETURN_TRACKER;
    }
}
