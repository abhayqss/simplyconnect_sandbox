package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.referrals.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.REFERRALS;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ReferralsWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ReferralsReport> {

    @Override
    public Workbook generateWorkbook(ReferralsReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addTotalReferralsSheet(workbook, styles, report.getTotalReferralsRows());
        var inReferralHeaderValues = Arrays
                .asList("Community name", "Referring Community", "Total # of Referrals", "# of Pending Referrals", "# of Pre-admit Referrals", "# of Accepted Referrals", "# of Declined Referrals", "# of Canceled Referrals");
        addReferReferralsSheet(workbook, styles, "Inbound referrals", inReferralHeaderValues, report.getInReferReferralsRows());
        addInboundIndividualReferReferralsSheet(workbook, styles, report.getInIndividualReferReferralRows());
        var outReferralHeaderValues = Arrays
                .asList("Community name", "Referred to community", "Total # of Referrals", "# of Pending Referrals", "# of Pre-admit Referrals", "# of Accepted Referrals", "# of Declined Referrals", "# of Canceled Referrals");
        addReferReferralsSheet(workbook, styles, "Outbound referrals", outReferralHeaderValues, report.getOutReferReferralsRows());
        addOutboundIndividualReferReferralsSheet(workbook, styles, report.getOutIndividualReferReferralRows());
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return REFERRALS;
    }

    private void addTotalReferralsSheet(Workbook workbook, Map<String, CellStyle> styles, List<TotalReferralsRow> totalReferralsRows) {
        String sheetName = "In & Out, total";
        var headerValues = Arrays.asList("Community name", "Total # of Inbound Referrals", "Total # of Outbound Referrals");
        Sheet sheet = createSheetWithHeader(workbook, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var refRow : Optional.ofNullable(totalReferralsRows).orElse(Collections.emptyList())) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), refRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getTotalNumberOfInboundReferrals());
            writeToCell(styles, row.createCell(colCount), refRow.getTotalNumberOfOutboundReferrals());
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addReferReferralsSheet(Workbook workbook, Map<String, CellStyle> styles, String sheetName, List<String> headerValues, List<ReferReferralsRow> referralsRows) {
        Sheet sheet = createSheetWithHeader(workbook, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var refRow : Optional.ofNullable(referralsRows).orElse(Collections.emptyList())) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), refRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getReferCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getTotalNumberOfReferrals());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfPendingReferrals());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfPreadmitReferrals());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfAcceptedReferrals());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfDeclinedReferrals());
            writeToCell(styles, row.createCell(colCount), refRow.getNumberOfCanceledReferrals());
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addInboundIndividualReferReferralsSheet(Workbook workbook, Map<String, CellStyle> styles, List<InboundIndividualReferReferralRow> referringReferralRows) {
        var sheetName = "Inbound by referring Individual";
        var headerValues = Arrays
                .asList("Community name", "Referring Community", "Referring Individual (physician)", "# of Referred Clients", "# of Accepted Referrals", "Referral Success Rate");
        Sheet sheet = createSheetWithHeader(workbook, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var refRow : Optional.ofNullable(referringReferralRows).orElse(Collections.emptyList())) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), refRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getReferCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getReferringIndividualName());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfReferredClients());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfAcceptedReferrals());
            writeToCell(styles, row.createCell(colCount), refRow.getReferralSuccessRate());
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addOutboundIndividualReferReferralsSheet(Workbook workbook, Map<String, CellStyle> styles, List<OutboundIndividualReferReferralRow> referredReferralRows) {
        var sheetName = "Outbound by referring Individual";
        var headerValues = Arrays
                .asList("Community name", "Referred to community", "Referring Individual (physician)", "# of Accepted Referrals", "Referral Success Rate");
        Sheet sheet = createSheetWithHeader(workbook, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        for (var refRow : Optional.ofNullable(referredReferralRows).orElse(Collections.emptyList())) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), refRow.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getReferCommunityName());
            writeToCell(styles, row.createCell(colCount++), refRow.getReferringIndividualName());
            writeToCell(styles, row.createCell(colCount++), refRow.getNumberOfAcceptedReferrals());
            writeToCell(styles, row.createCell(colCount), refRow.getReferralSuccessRate());
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
