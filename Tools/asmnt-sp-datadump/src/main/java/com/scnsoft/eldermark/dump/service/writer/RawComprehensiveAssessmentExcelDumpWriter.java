package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.model.RawComprehensiveAssessmentDump;
import com.scnsoft.eldermark.dump.model.RawComprehensiveAssessmentDumpEntry;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentElement;
import com.scnsoft.eldermark.dump.model.assessment.AssessmentPage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RawComprehensiveAssessmentExcelDumpWriter extends RawAssessmentExcelDumpWriter<RawComprehensiveAssessmentDump> {


    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a (z)");

    @Override
    public void writeDump(RawComprehensiveAssessmentDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.RAW_COMPREHENSIVE_ASSESSMENT;
    }

    private Workbook generateWorkBook(RawComprehensiveAssessmentDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);

        var structure = dump.getAssessmentStructure();

        CollectionUtils.emptyIfNull(structure.getPages()).stream()
                .filter(this::hasQuestions)
                .forEach(page -> addPage(workbook, styles, page, dump.getEntries()));

        return workbook;
    }

    private void addPage(XSSFWorkbook wb, Map<String, CellStyle> styles, AssessmentPage page, List<RawComprehensiveAssessmentDumpEntry> entries) {
        System.out.println("Adding page " + page.getName());
        var sheet = wb.createSheet(page.getName());

        var rowCount = 0;
        boolean addDateCompleted = page.getName().equals("Demographics");

        var additionalHeaderColumns = new ArrayList<>(Arrays.asList("Community Name", "Client id", "Client name"));
        if (addDateCompleted) {
            additionalHeaderColumns.add("Date Completed");
        }

        var colIdxQuestionMapping = new HashMap<Integer, String>();
        var headerHeight = writeAssessmentHeaderRows(sheet, styles, page, colIdxQuestionMapping, rowCount, additionalHeaderColumns.size());
        writeAdditionalHeader(sheet, styles, additionalHeaderColumns, rowCount, 0, headerHeight);

        rowCount = rowCount + headerHeight;

        for (var entry : entries) {
            var colCount = 0;
            var row = sheet.createRow(rowCount++);

            writeToCellOrEmpty(styles, row.createCell(colCount++), entry.getCommunityName());
            row.createCell(colCount++).setCellValue(entry.getClientId());
            writeToCellOrEmpty(styles, row.createCell(colCount++), entry.getClientName());

            if (addDateCompleted) {
                var dateCompleted = format(entry.getDateCompleted().atZone(TimeZone.getDefault().toZoneId()), TimeZone.getDefault().toZoneId(), DATE_TIME_FORMATTER);
                writeToCellOrEmpty(styles, row.createCell(colCount++), dateCompleted);
            }

            for (; colCount - additionalHeaderColumns.size() < colIdxQuestionMapping.size(); ++colCount) {
                var questionKey = colIdxQuestionMapping.get(colCount);
                var responseObj = entry.getResponses().get(questionKey);
                //todo handle list responses
                var response = responseObj != null ? responseObj.toString() : null;
                writeToCellOrEmpty(styles, row.createCell(colCount), response);
            }
        }

        autosizeWidth(sheet, colIdxQuestionMapping.size() + 1 + 1);
    }

    @Override
    protected boolean isQuestion(AssessmentElement element) {
        return super.isQuestion(element) && !Boolean.TRUE.equals(element.getIsPriority());
    }
}
