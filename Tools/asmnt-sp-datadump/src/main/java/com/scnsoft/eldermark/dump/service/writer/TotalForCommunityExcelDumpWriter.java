package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TotalForCommunityExcelDumpWriter extends ExcelDumpWriter implements DumpWriter<TotalForCommunityDump> {

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_COMMUNITY;
    }

    @Override
    public void writeDump(TotalForCommunityDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    Workbook generateWorkBook(TotalForCommunityDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addCommunityAssessmentsGeneral(workbook, styles, dump.getCommunityAssessmentsGeneralList());
        addGad7PHQ9Scoring(workbook, styles, dump.getGad7PHQ9ScoringList());
        addComprehensiveDetails(workbook, styles, dump.getComprehensiveDetailList());
        addCommunitySPGeneralList(workbook, styles, dump.getCommunitySPGeneralList());
        addSpIndividuals(workbook, styles, dump.getSpIndividualList());
        addSpDetails(workbook, styles, dump.getSpDetailList());
        addAssessedClientInfo(workbook, styles, dump.getAssessedClientInsuranceInfoList().stream().filter(info -> info.getCompletedComprehensiveCount() > 0).collect(Collectors.toList()));
        addNotAssessedClientInfo(workbook, styles, dump.getAssessedClientInsuranceInfoList().stream().filter(info -> info.getCompletedComprehensiveCount() == 0).collect(Collectors.toList()));

        return workbook;
    }

    private void addNotAssessedClientInfo(XSSFWorkbook wb, Map<String, CellStyle> styles, List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList) {
        var sheet = wb.createSheet("Not assessed clients");
        fillAssessedClientInfo(wb, styles, assessedClientInsuranceInfoList, sheet);

    }

    private void addAssessedClientInfo(XSSFWorkbook wb, Map<String, CellStyle> styles, List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList) {
        var sheet = wb.createSheet("Assessed clients");
        fillAssessedClientInfo(wb, styles, assessedClientInsuranceInfoList, sheet);
    }

    void fillAssessedClientInfo(XSSFWorkbook wb, Map<String, CellStyle> styles, List<AssessedClientInsuranceInfo> assessedClientInsuranceInfoList, Sheet sheet) {

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        var headerValues = Arrays.asList("ID", "First Name*", "Last Name*", "Organization*", "Community*", "Insurance Network", "Insurance Plan", "# completed Comprehensive");

        writeHeader(headerRow, styles, headerValues);

        for (var value : assessedClientInsuranceInfoList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getResidentId());
            writeToCell(styles, row.createCell(colCount++), value.getFirstName());
            writeToCell(styles, row.createCell(colCount++), value.getLastName());
            writeToCell(styles, row.createCell(colCount++), value.getOrganization());
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());

            writeToCell(styles, row.createCell(colCount++), value.getInsuranceNetwork());
            writeToCell(styles, row.createCell(colCount++), value.getInsurancePlan());

            writeToCell(styles, row.createCell(colCount++), value.getCompletedComprehensiveCount());

            if (!value.isActive()) {
                writeToCell(styles, row.createCell(colCount++), "inactive patient");
            }
        }

        autosizeWidth(sheet, headerValues.size());

    }


    void addCommunityAssessmentsGeneral(Workbook wb, Map<String, CellStyle> styles, List<CommunityAssessmentsGeneral> communityAssessmentsGeneralList) {
        var sheet = wb.createSheet("Assessments - General");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community Name", "GAD-7 completed", "PHQ-9 completed", "Comprehensive completed", "Total completed");

        writeHeader(headerRow, styles, headerValues);


        for (var value : communityAssessmentsGeneralList) {
            var row = sheet.createRow(rowCount++);

            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), value.getGad7Completed());
            writeToCell(styles, row.createCell(colCount++), value.getPhq9Completed());
            writeToCell(styles, row.createCell(colCount++), value.getComprehensiveCompleted());
            writeToCell(styles, row.createCell(colCount++), value.getGad7Completed() + value.getPhq9Completed() + value.getComprehensiveCompleted());
        }


        autosizeWidth(sheet, headerValues.size());
    }

    private void addCommunitySPGeneralList(XSSFWorkbook wb, Map<String, CellStyle> styles, List<CommunitySPGeneral> communitySPGeneralList) {
        var sheet = wb.createSheet("SP - General");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList(
                "Community Name",
                "# of patients",
                "# of service plans opened",
                "Average # of service plans per resident",
                "# of service plans closed",
                "total # of needs",
                "total # of goals",
                "average # of needs per resident",
                "average # of goals per resident",
                "# of accomplished goals",
                "% of accomplished goals"
        );

        writeHeader(headerRow, styles, headerValues);


        for (var value : communitySPGeneralList) {
            var row = sheet.createRow(rowCount++);

            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), value.getNumberOfPatients());
            writeToCell(styles, row.createCell(colCount++), value.getNumberOfSPopened());

            writeFormulaToCell(styles, row.createCell(colCount++), String.format("CEILING(((E%d + C%d) / B%d), 0.01)", rowCount, rowCount, rowCount), 0);

            writeToCell(styles, row.createCell(colCount++), value.getNumberOfSpClosed());
            writeToCell(styles, row.createCell(colCount++), value.getTotalNumberOfNeeds());
            writeToCell(styles, row.createCell(colCount++), value.getTotalNumberOfGoals());

            writeFormulaToCell(styles, row.createCell(colCount++), String.format("CEILING((F%d/B%d), 0.01)", rowCount, rowCount), 0);
            writeFormulaToCell(styles, row.createCell(colCount++), String.format("CEILING((G%d/B%d), 0.01)", rowCount, rowCount), 0);

            writeToCell(styles, row.createCell(colCount++), value.getNumberOfAccomplishedGoals());

            writeFormulaToCell(styles, row.createCell(colCount++), String.format("CEILING((J%d/G%d), 0.001)*100", rowCount, rowCount), 0);
        }

        autosizeWidth(sheet, headerValues.size());
    }


}
