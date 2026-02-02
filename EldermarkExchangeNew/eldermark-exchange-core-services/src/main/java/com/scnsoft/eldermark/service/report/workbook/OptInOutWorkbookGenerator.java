package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReport;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReportClientRow;
import com.scnsoft.eldermark.beans.reports.model.optinout.OptInOutReportCommunityRow;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class OptInOutWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<OptInOutReport> {

    private final static List<String> OPT_IN_OUT_REPORT_HEADERS = List.of(
            "Community Name",
            "Community Default Opt In /Opt Out Policy",
            "Client ID",
            "Client Status",
            "Client Name",
            "Opt In/Opt Out Status",
            "Obtained From",
            "Obtained By",
            "Date Obtained",
            "Source"
    );

    public static final String OPT_IN_OUT_SHEET_NAME = "Opt In & Opt Out";

    @Override
    public Workbook generateWorkbook(OptInOutReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addOptInOutTab(workbook, styles, report);
        return workbook;
    }

    private void addOptInOutTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, OptInOutReport report) {
        var headerValues = OPT_IN_OUT_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, OPT_IN_OUT_SHEET_NAME, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getRows();
        for (var itRow : itRows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            for (var communityRow : itRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                var defaultCommunityPolicies = communityRow.getDefaultCommunityPolicies().stream()
                        .map(policy -> createCommunityDefaultHieConsentPolicy(report.getTimeZoneOffset(), policy))
                        .collect(Collectors.toList());
                writeWrappedListTextNewLineToCell(
                        styles, row.createCell(communityColCount++), defaultCommunityPolicies
                );
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = communityColCount;
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientId());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getClientStatus());
                    writeToCellEmptyIfNa(styles, row.createCell(clientColCount++), clientRow.getFullClientName());

                    var policyStatusWithDates = clientRow.getPolicies().stream()
                            .map(policy -> createClientHieConsentPolicy(policy, report.getTimeZoneOffset()))
                            .collect(Collectors.toList());
                    writeWrappedListTextNewLineToCell(styles, row.createCell(clientColCount++), policyStatusWithDates);

                    var obtainedFroms = clientRow.getPolicies().stream()
                            .map(policy -> policy.getObtainedFrom() != null
                                    ? policy.getObtainedFrom()
                                    : StringUtils.EMPTY)
                            .collect(Collectors.toList());
                    writeWrappedListTextNewLineToCell(styles, row.createCell(clientColCount++), obtainedFroms);

                    var obtainedBys = clientRow.getPolicies().stream()
                            .map(policy -> policy.getObtainedBy() != null
                                    ? policy.getObtainedBy().getDisplayName()
                                    : StringUtils.EMPTY)
                            .collect(Collectors.toList());
                    writeWrappedListTextNewLineToCell(styles, row.createCell(clientColCount++), obtainedBys);
                    var obtainedDates = clientRow.getPolicies().stream()
                            .map(policy -> policy.getObtainedDate() != null ? policy.getObtainedDate() : null)
                            .collect(Collectors.toList());
                    writeToCellWithNullableDateList(styles, row.createCell(clientColCount++), obtainedDates, report.getTimeZoneOffset());
                    var sources = clientRow.getPolicies().stream()
                            .map(policy -> policy.getSource() != null ? policy.getSource().getDisplayName() : StringUtils.EMPTY)
                            .collect(Collectors.toList());
                    writeWrappedListTextNewLineToCell(styles, row.createCell(clientColCount++), sources);
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private String createCommunityDefaultHieConsentPolicy(Integer timeZoneOffset, OptInOutReportCommunityRow.DefaultCommunityHieConsentPolicy policy) {
        var policyName = policy.getHieConsentPolicy() != null ? policy.getHieConsentPolicy().getReportDisplayName() : null;
        if (policy.getLastModifiedDate() != null) {
            return policyName + " - " + formatToDate(policy.getLastModifiedDate(), timeZoneOffset);
        }
        return policyName;
    }

    private String createClientHieConsentPolicy(OptInOutReportClientRow.HieConsentPolicy policy, Integer timezoneOffset) {
        if (policy.getStatus() != null) {
            if (policy.getStatusUpdateTime() != null) {
                return policy.getStatus().getReportDisplayName() + " - " + formatToDate(policy.getStatusUpdateTime(), timezoneOffset);
            } else {
                return policy.getStatus().getReportDisplayName();
            }
        }
        return null;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.OPT_IN_OUT;
    }
}
