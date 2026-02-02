package com.scnsoft.eldermark.service.report.workbook;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.autosizeWidth;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.createStyles;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCell;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCellEmptyIfNa;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeWrappedTextToCell;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.signature.SignatureRequestReport;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureHistoryService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SignatureRequestWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<SignatureRequestReport> {

    private static final String SIGNATURES_REQUESTS_TAB_TITLE = "Signatures Requests";
    private static final List<String> SIGNATURE_REQUEST_REPORT_HEADERS = List.of(
        "Organization Name",
        "Community Name",
        "Client ID",
        "Client Name",
        "Document Template",
        "Signature status",
        "By",
        "Role",
        "Date & Time",
        "Comments"
    );

    @Autowired
    private DocumentSignatureHistoryService documentSignatureHistoryService;

    @Override
    public Workbook generateWorkbook(SignatureRequestReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addSignatureRequestTab(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.SIGNATURE_REQUESTS;
    }

    private void addSignatureRequestTab(XSSFWorkbook workbook, Map<String, CellStyle> styles, SignatureRequestReport report) {
        var headerValues = SIGNATURE_REQUEST_REPORT_HEADERS;
        Sheet sheet = createSheetWithHeader(workbook, styles, SIGNATURES_REQUESTS_TAB_TITLE, headerValues, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headerValues.size() - 1));

        var rowCount = 1;
        var itRows = report.getRows();
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
                    for (var documentRow: clientRow.getDocumentRows()) {
                        row = getOrCreateRow(sheet, rowCount);
                        var documentColCount = clientColCount;
                        writeToCell(styles, row.createCell(documentColCount++), documentRow.getTemplateName());
                        for (var actionRow: documentRow.getActionRows()) {
                            row = getOrCreateRow(sheet, rowCount++);
                            var actionColCount = documentColCount;
                            writeToCell(styles, row.createCell(actionColCount++), actionRow.getSignatureStatusName());
                            writeToCellEmptyIfNa(styles, row.createCell(actionColCount++), actionRow.getActorName());
                            writeToCellEmptyIfNa(styles, row.createCell(actionColCount++), actionRow.getActorRoleName());
                            writeToCell(styles, row.createCell(actionColCount++), DateTimeUtils.formatDateTime(
                                    actionRow.getActionDateTime(), report.getTimeZoneOffset())
                            );
                            writeWrappedTextToCell(
                                    styles,
                                    row.createCell(actionColCount++),
                                    documentSignatureHistoryService.generateCommentsForHistory(
                                            actionRow.getComments(),
                                            report.getTimeZoneOffset()
                                    )
                            );
                        }
                    }
                }
            }
        }
        autosizeWidth(sheet, headerValues.size() + 1);
    }
}
